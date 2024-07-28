// import React from 'react'
// import ReactDOM from 'react-dom/client'
// import { BrowserRouter } from 'react-router-dom'
// import App from './App.tsx'
// import './index.css'
//
// ReactDOM.createRoot(document.getElementById('root')!).render(
//   <React.StrictMode>
//     <BrowserRouter>
//       <App />
//     </BrowserRouter>
//   </React.StrictMode>,
// )

import React from 'react'
import ReactDOM from 'react-dom/client'
import { App } from './app.tsx'
import './styles/index.css'

ReactDOM.createRoot(document.getElementById('noteverso-app')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)
