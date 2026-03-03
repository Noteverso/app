import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { BrowserRouter } from 'react-router-dom'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import { SignupPage } from '../signup-page'
import * as userApi from '@/api/user/user'

vi.mock('@/api/user/user')

const mockNavigate = vi.fn()
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom')
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  }
})

const renderSignupPage = () => {
  return render(
    <BrowserRouter>
      <SignupPage />
    </BrowserRouter>
  )
}

describe('SignupPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render signup form', () => {
    renderSignupPage()
    expect(screen.getByLabelText(/邮箱/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/用户名/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/^密码$/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/确认密码/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/验证码/i)).toBeInTheDocument()
  })

  it('should show validation errors for invalid inputs', async () => {
    const user = userEvent.setup()
    renderSignupPage()

    const submitButton = screen.getByRole('button', { name: /sign up/i })
    await user.click(submitButton)

    await waitFor(() => {
      expect(screen.getByText(/email is required/i)).toBeInTheDocument()
    })
  })

  it('should show error when passwords do not match', async () => {
    const user = userEvent.setup()
    renderSignupPage()

    await user.type(screen.getByLabelText(/邮箱/i), 'test@example.com')
    await user.type(screen.getByLabelText(/用户名/i), 'testuser')
    await user.type(screen.getByLabelText(/^密码$/i), 'Password123')
    await user.type(screen.getByLabelText(/确认密码/i), 'DifferentPass123')
    await user.type(screen.getByLabelText(/验证码/i), '123456')

    await user.click(screen.getByRole('button', { name: /sign up/i }))

    await waitFor(() => {
      expect(screen.getByText(/passwords don't match/i)).toBeInTheDocument()
    })
  })

  it('should send captcha when email is valid', async () => {
    const user = userEvent.setup()
    vi.mocked(userApi.sendCaptchaApi).mockResolvedValue({ ok: true, data: 'Success' })

    renderSignupPage()

    await user.type(screen.getByLabelText(/邮箱/i), 'test@example.com')
    await user.click(screen.getByRole('button', { name: /send/i }))

    await waitFor(() => {
      expect(userApi.sendCaptchaApi).toHaveBeenCalledWith('test@example.com')
    })
  })

  it('should disable captcha button during countdown', async () => {
    const user = userEvent.setup()
    vi.mocked(userApi.sendCaptchaApi).mockResolvedValue({ ok: true, data: 'Success' })

    renderSignupPage()

    await user.type(screen.getByLabelText(/邮箱/i), 'test@example.com')
    const sendButton = screen.getByRole('button', { name: /send/i })
    await user.click(sendButton)

    await waitFor(() => {
      expect(sendButton).toBeDisabled()
    })
  })

  it('should submit form with valid data', async () => {
    const user = userEvent.setup()
    vi.mocked(userApi.signupApi).mockResolvedValue({ ok: true, data: 'Success' })

    renderSignupPage()

    await user.type(screen.getByLabelText(/邮箱/i), 'test@example.com')
    await user.type(screen.getByLabelText(/用户名/i), 'testuser')
    await user.type(screen.getByLabelText(/^密码$/i), 'Password123')
    await user.type(screen.getByLabelText(/确认密码/i), 'Password123')
    await user.type(screen.getByLabelText(/验证码/i), '123456')

    await user.click(screen.getByRole('button', { name: /sign up/i }))

    await waitFor(() => {
      expect(userApi.signupApi).toHaveBeenCalledWith({
        email: 'test@example.com',
        username: 'testuser',
        password: 'Password123',
        captchaCode: '123456',
      })
    })
  })

  it('should navigate to login after successful signup', async () => {
    const user = userEvent.setup()
    vi.mocked(userApi.signupApi).mockResolvedValue({ ok: true, data: 'Success' })

    renderSignupPage()

    await user.type(screen.getByLabelText(/邮箱/i), 'test@example.com')
    await user.type(screen.getByLabelText(/用户名/i), 'testuser')
    await user.type(screen.getByLabelText(/^密码$/i), 'Password123')
    await user.type(screen.getByLabelText(/确认密码/i), 'Password123')
    await user.type(screen.getByLabelText(/验证码/i), '123456')

    await user.click(screen.getByRole('button', { name: /sign up/i }))

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalled()
    }, { timeout: 2000 })
  })
})
