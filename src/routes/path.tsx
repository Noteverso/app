export const ROUTER_PATHS = {
  HOME: {
    path: '/',
    name: '首页',
  },
  INBOX: {
    path: '/app/inbox',
    // name: 'Inbox',
    name: '收件箱',
  },
  LABELS: {
    path: '/app/labels',
    // name: 'Labels',
    name: '标签',
  },
  ATTACHMENTS: {
    path: '/app/attachments',
    // name: 'Attachments',
    name: '附件',
  },
  PROJECTS: {
    path: '/app/projects',
    name: 'Projects',
  },
  LOGIN: {
    path: '/auth/login',
    // name: 'Login',
    name: '登录',
  },
  SIGNUP: {
    path: '/auth/signup',
    // name: 'Signup',
    name: '注册',
  },
  LOGOUT: {
    path: '/auth/logout',
    // name: 'Logout',
    name: '退出登陆',
  },
} as const
