import type React from 'react'
import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import type * as ReactRouterDom from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { Label } from './label'

const labelTestState = vi.hoisted(() => ({
  navigate: vi.fn(),
  toast: vi.fn(),
  getLabelsApi: vi.fn(),
  createLabelApi: vi.fn(),
  updateLabelApi: vi.fn(),
  deleteLabelApi: vi.fn(),
}))

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual<typeof ReactRouterDom>('react-router-dom')

  return {
    ...actual,
    useNavigate: () => labelTestState.navigate,
  }
})

vi.mock('@/api/label/label', () => ({
  getLabelsApi: labelTestState.getLabelsApi,
  createLabelApi: labelTestState.createLabelApi,
  updateLabelApi: labelTestState.updateLabelApi,
  deleteLabelApi: labelTestState.deleteLabelApi,
}))

vi.mock('@/components/ui/toast/use-toast', () => ({
  useToast: () => ({
    toast: labelTestState.toast,
    dismiss: vi.fn(),
    toasts: [],
  }),
}))

vi.mock('@/components/ui/button/button', () => ({
  Button: ({ children, ...props }: React.ComponentProps<'button'>) => <button {...props}>{children}</button>,
}))

vi.mock('@/components/ui/dialog/dialog', () => ({
  Dialog: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  DialogContent: () => null,
  DialogHeader: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  DialogTitle: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  DialogTrigger: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
}))

vi.mock('@/components/ui/input/input', () => ({
  Input: (props: React.ComponentProps<'input'>) => <input {...props} />,
}))

vi.mock('@/components/ui/alert-dialog/alert-dialog', () => ({
  AlertDialog: ({ open, children }: { open?: boolean; children: React.ReactNode }) => (open ? <div>{children}</div> : null),
  AlertDialogAction: ({ children, onClick, disabled }: React.ComponentProps<'button'>) => (
    <button type="button" onClick={onClick} disabled={disabled}>
      {children}
    </button>
  ),
  AlertDialogCancel: ({ children, onClick, disabled }: React.ComponentProps<'button'>) => (
    <button type="button" onClick={onClick} disabled={disabled}>
      {children}
    </button>
  ),
  AlertDialogContent: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  AlertDialogDescription: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  AlertDialogFooter: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  AlertDialogHeader: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
  AlertDialogTitle: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
}))

describe('Label delete behavior', () => {
  beforeEach(() => {
    labelTestState.navigate.mockReset()
    labelTestState.toast.mockReset()
    labelTestState.getLabelsApi.mockReset()
    labelTestState.createLabelApi.mockReset()
    labelTestState.updateLabelApi.mockReset()
    labelTestState.deleteLabelApi.mockReset()
    labelTestState.getLabelsApi.mockResolvedValue({
      ok: true,
      data: [
        {
          labelId: 'label-1',
          name: 'Urgent',
          color: '#ef4444',
          noteCount: 2,
        },
      ],
    })
    labelTestState.deleteLabelApi.mockResolvedValue({
      ok: true,
    })
  })

  it('does not show a success toast after deleting a label', async () => {
    render(<Label />)

    await waitFor(() => expect(labelTestState.getLabelsApi).toHaveBeenCalledTimes(1))
    expect(screen.getByText('Urgent')).toBeInTheDocument()

    fireEvent.click(screen.getAllByRole('button')[2])
    fireEvent.click(screen.getByRole('button', { name: 'Delete' }))

    await waitFor(() => expect(labelTestState.deleteLabelApi).toHaveBeenCalledWith('label-1'))
    expect(labelTestState.toast).not.toHaveBeenCalled()
  })
})
