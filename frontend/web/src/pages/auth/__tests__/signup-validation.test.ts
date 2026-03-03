import { z } from 'zod'

const formSchema = z.object({
  email: z.string().min(1, 'Email is required').email('Invalid email address'),
  username: z.string()
    .min(3, 'Username must be at least 3 characters')
    .max(20, 'Username must not exceed 20 characters')
    .regex(/^[a-zA-Z0-9_]+$/, 'Username can only contain letters, numbers, and underscores'),
  password: z.string()
    .min(8, 'Password must be at least 8 characters')
    .max(50, 'Password must not exceed 50 characters')
    .regex(/[A-Z]/, 'Password must contain at least one uppercase letter')
    .regex(/[a-z]/, 'Password must contain at least one lowercase letter')
    .regex(/[0-9]/, 'Password must contain at least one number'),
  confirmPassword: z.string().min(1, 'Please confirm your password'),
  captchaCode: z.string()
    .length(6, 'Captcha code must be exactly 6 digits')
    .regex(/^\d+$/, 'Captcha code must contain only numbers'),
}).refine((data) => data.password === data.confirmPassword, {
  message: "Passwords don't match",
  path: ['confirmPassword'],
})

describe('Signup Form Validation', () => {
  const validData = {
    email: 'test@example.com',
    username: 'testuser',
    password: 'Password123',
    confirmPassword: 'Password123',
    captchaCode: '123456',
  }

  describe('Email validation', () => {
    it('should accept valid email', () => {
      expect(formSchema.safeParse(validData).success).toBe(true)
    })

    it('should reject empty email', () => {
      const result = formSchema.safeParse({ ...validData, email: '' })
      expect(result.success).toBe(false)
    })

    it('should reject invalid email format', () => {
      const result = formSchema.safeParse({ ...validData, email: 'invalid-email' })
      expect(result.success).toBe(false)
    })
  })

  describe('Username validation', () => {
    it('should accept valid username', () => {
      expect(formSchema.safeParse({ ...validData, username: 'user_123' }).success).toBe(true)
    })

    it('should reject username shorter than 3 characters', () => {
      const result = formSchema.safeParse({ ...validData, username: 'ab' })
      expect(result.success).toBe(false)
    })

    it('should reject username longer than 20 characters', () => {
      const result = formSchema.safeParse({ ...validData, username: 'a'.repeat(21) })
      expect(result.success).toBe(false)
    })

    it('should reject username with special characters', () => {
      const result = formSchema.safeParse({ ...validData, username: 'user@123' })
      expect(result.success).toBe(false)
    })
  })

  describe('Password validation', () => {
    it('should accept valid password', () => {
      expect(formSchema.safeParse(validData).success).toBe(true)
    })

    it('should reject password shorter than 8 characters', () => {
      const result = formSchema.safeParse({ ...validData, password: 'Pass1', confirmPassword: 'Pass1' })
      expect(result.success).toBe(false)
    })

    it('should reject password without uppercase letter', () => {
      const result = formSchema.safeParse({ ...validData, password: 'password123', confirmPassword: 'password123' })
      expect(result.success).toBe(false)
    })

    it('should reject password without lowercase letter', () => {
      const result = formSchema.safeParse({ ...validData, password: 'PASSWORD123', confirmPassword: 'PASSWORD123' })
      expect(result.success).toBe(false)
    })

    it('should reject password without number', () => {
      const result = formSchema.safeParse({ ...validData, password: 'Password', confirmPassword: 'Password' })
      expect(result.success).toBe(false)
    })

    it('should reject mismatched passwords', () => {
      const result = formSchema.safeParse({ ...validData, confirmPassword: 'DifferentPass123' })
      expect(result.success).toBe(false)
      if (!result.success) {
        expect(result.error.issues[0].path).toContain('confirmPassword')
      }
    })
  })

  describe('Captcha validation', () => {
    it('should accept valid 6-digit captcha', () => {
      expect(formSchema.safeParse(validData).success).toBe(true)
    })

    it('should reject captcha with less than 6 digits', () => {
      const result = formSchema.safeParse({ ...validData, captchaCode: '12345' })
      expect(result.success).toBe(false)
    })

    it('should reject captcha with more than 6 digits', () => {
      const result = formSchema.safeParse({ ...validData, captchaCode: '1234567' })
      expect(result.success).toBe(false)
    })

    it('should reject captcha with non-numeric characters', () => {
      const result = formSchema.safeParse({ ...validData, captchaCode: '12345a' })
      expect(result.success).toBe(false)
    })
  })
})
