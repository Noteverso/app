import { useActionData } from 'react-router-dom'
import { useEffect } from 'react'
import { useToast } from '@/components/toast/use-toast'
import type { ErrorDetail } from '@/types/common'

export const useActionResult = () => {
  const actionData = useActionData() as ErrorDetail
  const { toast } = useToast()

  useEffect(() => {
    if (actionData && !actionData.ok) {
      toast({
        variant: 'destructive',
        title: '错误',
        description: actionData.message,
      })
    }
  }, [actionData, toast])

  return actionData
}
