import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { Input } from '@/components/ui/input/input'
import { FormControl, FormField, FormItem, FormLabel, FormMessage, Form } from '@/components/ui/form'
import { Button } from '@/components/ui/button/button'
import { ROUTER_PATHS } from '@/constants'
import { signupApi, sendCaptchaApi } from '@/api/user/user'
import { useToast } from '@/components/ui/toast/use-toast'

const formSchema = z.object({
  email: z.string().min(1, 'Email is required').email('Invalid email address'),
  username: z.string()
    .min(3, 'Username must be at least 3 characters')
    .max(20, 'Username must not exceed 20 characters')
    .regex(/^[a-zA-Z0-9_]+$/, 'Username can only contain letters, numbers, and underscores'),
  password: z.string()
    .min(8, 'Password must be at least 8 characters')
    .max(50, 'Password must not exceed 50 characters')
    .regex(/[A-Z]/, 'Password must contain at least one uppercase letter')
    .regex(/[a-z]/, 'Password must contain at least one lowercase letter')
    .regex(/[0-9]/, 'Password must contain at least one number'),
  confirmPassword: z.string().min(1, 'Please confirm your password'),
  captchaCode: z.string()
    .length(6, 'Captcha code must be exactly 6 digits')
    .regex(/^\d+$/, 'Captcha code must contain only numbers'),
}).refine((data) => data.password === data.confirmPassword, {
  message: "Passwords don't match",
  path: ['confirmPassword'],
})

export function SignupPage() {
  const [sending, setSending] = useState(false)
  const [countdown, setCountdown] = useState(0)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const navigate = useNavigate()
  const { toast } = useToast()

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      email: '',
      username: '',
      password: '',
      confirmPassword: '',
      captchaCode: '',
    },
  })

  const handleSendCaptcha = async () => {
    const email = form.getValues('email')
    if (!email || !z.string().email().safeParse(email).success) {
      form.setError('email', { message: 'Please enter a valid email' })
      return
    }

    setSending(true)
    try {
      const response = await sendCaptchaApi(email)
      if (response.ok) {
        toast({ title: 'Captcha sent successfully' })
        setCountdown(60)
        const timer = setInterval(() => {
          setCountdown((prev) => {
            if (prev <= 1) {
              clearInterval(timer)
              return 0
            }
            return prev - 1
          })
        }, 1000)
      } else {
        toast({ title: response.data.error.message, variant: 'destructive' })
      }
    } catch (error: any) {
      toast({ title: error.message || 'Failed to send captcha', variant: 'destructive' })
    } finally {
      setSending(false)
    }
  }

  const onSubmit = async (values: z.infer<typeof formSchema>) => {
    setIsSubmitting(true)
    try {
      const { confirmPassword, ...signupData } = values
      const response = await signupApi(signupData)
      if (response.ok) {
        toast({ title: 'Account created successfully! Please login.' })
        setTimeout(() => navigate(ROUTER_PATHS.LOGIN.path), 1000)
      } else {
        toast({ title: response.data.error.message, variant: 'destructive' })
      }
    } catch (error: any) {
      toast({ title: error.message || 'Signup failed', variant: 'destructive' })
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="w-full h-full max-w-sm mx-auto mt-48">
      <h2 className="text-3xl font-bold mb-4">注册</h2>
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
          <FormField
            control={form.control}
            name="email"
            render={({ field }) => (
              <FormItem>
                <FormLabel>邮箱</FormLabel>
                <FormControl>
                  <Input {...field} type="email" placeholder="Enter your email" />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="username"
            render={({ field }) => (
              <FormItem>
                <FormLabel>用户名</FormLabel>
                <FormControl>
                  <Input {...field} placeholder="Enter your username" />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="password"
            render={({ field }) => (
              <FormItem>
                <FormLabel>密码</FormLabel>
                <FormControl>
                  <Input {...field} type="password" placeholder="At least 8 characters" />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="confirmPassword"
            render={({ field }) => (
              <FormItem>
                <FormLabel>确认密码</FormLabel>
                <FormControl>
                  <Input {...field} type="password" placeholder="Confirm your password" />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="captchaCode"
            render={({ field }) => (
              <FormItem>
                <FormLabel>验证码</FormLabel>
                <div className="flex gap-2">
                  <FormControl>
                    <Input {...field} placeholder="Enter captcha code" />
                  </FormControl>
                  <Button
                    type="button"
                    onClick={handleSendCaptcha}
                    disabled={sending || countdown > 0}
                    variant="outline"
                  >
                    {countdown > 0 ? `${countdown}s` : sending ? 'Sending...' : 'Send'}
                  </Button>
                </div>
                <FormMessage />
              </FormItem>
            )}
          />
          <Button type="submit" className="w-full" disabled={isSubmitting}>
            {isSubmitting ? 'Signing up...' : 'Sign Up'}
          </Button>
          <p className="text-sm text-center text-gray-600">
            Already have an account?{' '}
            <Link to={ROUTER_PATHS.LOGIN.path} className="text-blue-600 hover:underline">
              Login
            </Link>
          </p>
        </form>
      </Form>
    </div>
  )
}
