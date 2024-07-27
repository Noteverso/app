import { Logo } from '@/components/logo/logo'

export function Footer() {
  return (
    <footer id="app-footer" className="grid place-items-center h-16 border-t-gray-900 border-t">
      <Logo className="has-[svg]:w-[45px]" />
    </footer>
  )
}
