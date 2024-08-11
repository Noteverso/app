import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { Form, Navigate, useActionData } from 'react-router-dom'
import { Input } from '@/components/input/input'
import { FormControl, FormField, FormItem, FormLabel, FormMessage, Form as FormProvider } from '@/components/form'
import { Button } from '@/components/button/button'
import { ROUTER_PATHS } from '@/constants'
import type { UserResponse } from '@/types/user'

const formSchema = z.object({
  username: z.string().email(),
  password: z.string().min(8, {
    message: 'Password must be at least 8 characters',
  }),
})

export function LoginPage() {
  // Define a form
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      username: '',
      password: '',
    },
  })

  const actionData = useActionData() as UserResponse
  if (actionData?.token) {
    return <Navigate to={ROUTER_PATHS.INBOX.path} replace />
  }

  return (
    <div className="w-full h-full max-w-sm mx-auto mt-48">
      <h2 className="text-3xl font-bold mb-4">登陆</h2>
      <FormProvider {...form}>
        <Form method="post" action={ROUTER_PATHS.LOGIN.path} className="space-y-4">
          <FormField
            control={form.control}
            name="username"
            render={({ field }) => (
              <FormItem>
                <FormLabel>邮箱</FormLabel>
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
                <FormLabel>密码</FormLabel>
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
        </Form>
      </FormProvider>
    </div>
  )
}
