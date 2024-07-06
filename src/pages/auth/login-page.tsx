import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'
import { UserForLogin } from '@/api/user'
import { Input } from '@/components/input/input'
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/form'
import { Button } from '@/components/button'
import { ROUTER_PATHS } from '@/routes/path'
import { authProvider } from '@/lib/auth'

const formSchema = z.object({
  username: z.string().email(),
  password: z.string().min(8, {
    message: 'Password must be at least 8 characters',
  }),
})

export function LoginPage() {
  const navicate = useNavigate()
  const location = useLocation()

  const params = new URLSearchParams(location.search)
  const from = location.state?.from?.pathname || params.get('from') || ROUTER_PATHS.INBOX.path

  // 1. Define a form
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      username: '',
      password: '',
    },
  })

  // 2. Define a submit handler
  async function onSubmit(values: z.infer<typeof formSchema>) {
    const user: UserForLogin = {
      username: values.username,
      password: values.password,
    }

    await authProvider.login(user)
    navicate(from, { replace: true })
  }

  if (authProvider.isAuthenticated()) {
    return <Navigate to={from} replace />
  }

  return (
    <div className="w-full h-full max-w-sm mx-auto">
      <h2 className="text-3xl font-bold mb-4">Log in</h2>
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
          <FormField
            control={form.control}
            name="username"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Email</FormLabel>
                <FormControl>
                  <Input
                    {...field}
                    type="email"
                    required
                    autoComplete="username"
                    placeholder="Enter your email"
                  />
                </FormControl>
                {/* <FormDescription>This is your email</FormDescription> */}
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="password"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Password</FormLabel>
                <FormControl>
                  <Input
                    {...field}
                    type="password"
                    required
                    autoComplete="current-password"
                    placeholder="Enter your password"
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <Button type="submit">Submit</Button>
        </form>
      </Form>
    </div>
  )
}
