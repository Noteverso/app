export const ROUTER_PATHS = {
  HOME: {
    path: '/',
    name: '首页',
  },
  INBOX: {
    path: '/app/inbox',
    name: 'Inbox',
  },
  LABELS: {
    path: '/app/labels',
    name: 'Labels',
  },
  ATTACHMENTS: {
    path: '/app/attachments',
    name: 'Attachments',
  },
  PROJECTS: {
    path: '/app/projects',
    name: 'Projects',
  },
  LOGIN: {
    path: '/auth/login',
    name: 'Login',
  },
  SIGNUP: {
    path: '/auth/signup',
    name: 'Signup',
  },
  LOGOUT: {
    path: '/auth/logout',
    name: 'Logout',
  },
} as const
