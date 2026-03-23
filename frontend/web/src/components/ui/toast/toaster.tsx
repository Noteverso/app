'use client'

import {
  Toast,
  ToastClose,
  ToastDescription,
  ToastProvider,
  ToastTitle,
  ToastViewport,
} from '@/components/ui/toast/toast'
import { useToast } from '@/components/ui/toast/use-toast'
import { cn } from '@/lib/utils'

export function Toaster() {
  const { toasts } = useToast()

  return (
        <ToastProvider>
            {toasts.map(({ id, title, description, action, contentClassName, titleClassName, descriptionClassName, closeClassName, ...props }) => {
              return (
                    <Toast key={id} {...props}>
                        <div className={cn('grid gap-1', contentClassName)}>
                            {title && <ToastTitle className={titleClassName}>{title}</ToastTitle>}
                            {description && (
                                <ToastDescription className={descriptionClassName}>{description}</ToastDescription>
                            )}
                        </div>
                        {action}
                        <ToastClose className={closeClassName} />
                    </Toast>
              )
            })}
            <ToastViewport />
        </ToastProvider>
  )
}
