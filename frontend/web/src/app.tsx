import { RouterProvider } from 'react-router-dom'
import { router } from '@/routes/routes'

export function App() {
  return (
    <RouterProvider router={router} />
  )
}

// import * as React from 'react'
// import {
//   Link,
//   Navigate,
//   Outlet,
//   Route,
//   Routes,
//   useLocation,
//   useNavigate,
// } from 'react-router-dom'
// import { fakeAuthProvider } from '@/contexts/fakeAuthProvider'
//
// export default function App() {
//   return (
//     <AuthProvider>
//       <h1>Auth Example</h1>
//
//       <p className="mb-4">
//         This example demonstrates a simple login flow with three pages: a public
//         page, a protected page, and a login page. In order to see the protected
//         page, you must first login. Pretty standard stuff.
//       </p>
//
//       <p className="mb-4">
//         First, visit the public page. Then, visit the protected page. You are not
//         yet logged in, so you are redirected to the login page. After you login,
//         you are redirected back to the protected page.
//       </p>
//
//       <p className="mb-4">
//         Notice the URL change each time. If you click the back button at this
//         point, would you expect to go back to the login page? No! You are already
//         logged in. Try it out, and you will see you go back to the page you
//         visited just *before* logging in, the public page.
//       </p>
//
//       <Routes>
//         <Route element={<Layout />}>
//           <Route path="/" element={<PublicPage />} />
//           <Route path="/login" element={<LoginPage />} />
//           <Route
//             path="/protected"
//             element={
//               <RequireAuth>
//                 <ProtectedPage />
//               </RequireAuth>
//             }
//           />
//         </Route>
//       </Routes>
//     </AuthProvider>
//   )
// }
//
// function Layout() {
//   return (
//     <div>
//       <AuthStatus />
//
//       <ul>
//         <li className="text-blue-400">
//           <Link to="/">Public Page</Link>
//         </li>
//         <li className="text-blue-400">
//           <Link to="/protected">Protected Page</Link>
//         </li>
//       </ul>
//
//       <Outlet />
//     </div>
//   )
// }
//
// interface AuthContextType {
//   user: any;
//   signin: (user: string, callback: VoidFunction) => void;
//   signout: (callback: VoidFunction) => void;
// }
//
// const AuthContext = React.createContext<AuthContextType>(null!)
//
// function AuthProvider({ children }: { children: React.ReactNode }) {
//   const [user, setUser] = React.useState<any>(null)
//
//   const signin = (newUser: string, callback: VoidFunction) => {
//     return fakeAuthProvider.signin(() => {
//       setUser(newUser)
//       callback()
//     })
//   }
//
//   const signout = (callback: VoidFunction) => {
//     return fakeAuthProvider.signout(() => {
//       setUser(null)
//       callback()
//     })
//   }
//
//   const value = { user, signin, signout }
//
//   return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
// }
//
// function useAuth() {
//   return React.useContext(AuthContext)
// }
//
// function AuthStatus() {
//   const auth = useAuth()
//   const navigate = useNavigate()
//
//   if (!auth.user) {
//     return <p>You are not logged in.</p>
//   }
//
//   return (
//     <p>
//       Welcome {auth.user}!{' '}
//       <button
//         onClick={() => {
//           auth.signout(() => navigate('/'))
//         }}
//       >
//         Sign out
//       </button>
//     </p>
//   )
// }
//
// function RequireAuth({ children }: { children: JSX.Element }) {
//   const auth = useAuth()
//   const location = useLocation()
//
//   if (!auth.user) {
//     // Redirect them to the /login page, but save the current location they were
//     // trying to go to when they were redirected. This allows us to send them
//     // along to that page after they login, which is a nicer user experience
//     // than dropping them off on the home page.
//     return <Navigate to="/login" state={{ from: location }} replace />
//   }
//
//   return children
// }
//
// function LoginPage() {
//   const navigate = useNavigate()
//   const location = useLocation()
//   const auth = useAuth()
//
//   const from = location.state?.from?.pathname || '/'
//   console.log(location)
//   console.log(from)
//
//   function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
//     event.preventDefault()
//
//     const formData = new FormData(event.currentTarget)
//     const username = formData.get('username') as string
//
//     auth.signin(username, () => {
//       // Send them back to the page they tried to visit when they were
//       // redirected to the login page. Use { replace: true } so we don't create
//       // another entry in the history stack for the login page.  This means that
//       // when they get to the protected page and click the back button, they
//       // won't end up back on the login page, which is also really nice for the
//       // user experience.
//       navigate(from, { replace: true })
//     })
//   }
//
//   return (
//     <div>
//       <p>You must log in to view the page at {from}</p>
//
//       <form onSubmit={handleSubmit}>
//         <label>
//           Username: <input name="username" type="text" />
//         </label>{' '}
//         <button type="submit">Login</button>
//       </form>
//     </div>
//   )
// }
//
// function PublicPage() {
//   return <h3>Public</h3>
// }
//
// function ProtectedPage() {
//   return <h3>Protected</h3>
// }
