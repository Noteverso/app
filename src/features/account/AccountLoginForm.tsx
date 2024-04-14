import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { useLocation, useNavigate } from 'react-router-dom'
import { Input } from '@/components/ui/input'
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { Button } from '@/components/ui/button/button'
import { useAuth } from '@/hooks/useAuth'
import { UserForLogin } from '@/api/user'

const formSchema = z.object({
  username: z.string().email(),
  password: z.string().min(8, {
    message: 'Password must be at least 8 characters',
  }),
})

function AccountLoginForm() {
  const auth = useAuth()
  const navicate = useNavigate()
  const location = useLocation()

  const from = location.state?.from?.pathname || '/'

  // 1. Define a form
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      username: '',
      password: '',
    },
  })

  // 2. Define a submit handler
  function onSubmit(values: z.infer<typeof formSchema>) {
    const user: UserForLogin = {
      username: values.username,
      password: values.password,
    }

    auth?.signin(user, () => {
      navicate(from, { replace: true })
    })
  }

  return (
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
  )
}

export default AccountLoginForm
