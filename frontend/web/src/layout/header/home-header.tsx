import { Link } from 'react-router-dom'
import styles from './header.module.css'
import type { UserResponse } from '@/types/user'
import { Logo } from '@/components/logo/logo'
import { ROUTER_PATHS } from '@/constants'

export function HomeHeader({ user }: { user: UserResponse }) {
  return (
    <header className={styles.header}>
      <Logo />
      <nav className={styles.headerNav}>
        <ul className={styles.headerList}>
          {user
            ? (
            <li className="flex">
              <Link to={ROUTER_PATHS.INBOX.path} className={styles.headerLink}>进入 Noteverso</Link>
            </li>)
            : <>
              <li className="flex">
                <Link to={ROUTER_PATHS.LOGIN.path} className={styles.headerLink}>{ROUTER_PATHS.LOGIN.name}</Link>
              </li>
              <li className="flex">
                <Link
                  to={ROUTER_PATHS.SIGNUP.path}
                  className={`${styles.headerLink} ${styles.headerLinkHighlight}`}>
                  {ROUTER_PATHS.SIGNUP.name}
                </Link>
              </li>
            </>
          }
        </ul>
      </nav>
    </header>
  )
}
