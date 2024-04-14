import { AuthProvider } from '@/contexts/AuthContext'
import { AccountLoginForm } from '@/features/account'

export default function LoginPage() {
  return (
    <AuthProvider>
      <div className="w-full h-full max-w-sm mx-auto">
        <h2 className="text-3xl font-bold mb-4">Log in</h2>
        <AccountLoginForm />
      </div>
    </AuthProvider>
  )
}
