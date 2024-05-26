export const ROUTER_PATHS = {
  HOME: {
    path: '/',
    name: 'home',
  },
  INBOX: {
    path: '/app/inbox',
    name: 'inbox',
  },
  LABELS: {
    path: '/app/labels',
    name: 'labels',
  },
  ATTACHMENTS: {
    path: '/app/attachments',
    name: 'attachments',
  },
  PROJECTS: {
    path: '/app/projects',
    name: 'projects',
  },
  LOGIN: {
    path: '/auth/login',
    name: 'login',
  },
  SIGNUP: {
    path: '/auth/signup',
    name: 'signup',
  },
  LOGOUT: {
    path: '/auth/logout',
    name: 'logout',
  },
} as const
