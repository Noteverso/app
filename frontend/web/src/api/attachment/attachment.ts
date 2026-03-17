import { request } from '@/lib/http'
import type { AttachmentPageResult } from '@/types/attachment'

export function getUserAttachmentsApi(pageIndex: number, pageSize: number) {
  return request<AttachmentPageResult>({
    url: '/api/v1/files/attachments',
    method: 'get',
    params: { pageIndex, pageSize },
  })
}

export function getAttachmentUrlApi(attachmentId: string) {
  return request<string>({
    url: `/api/v1/files/${attachmentId}`,
    method: 'get',
  })
}

export function deleteAttachmentApi(attachmentId: string) {
  return request<void>({
    url: `/api/v1/files/attachments/${attachmentId}`,
    method: 'delete',
  })
}

export function uploadFileApi(file: File, resourceType: string) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('resourceType', resourceType)

  return request<{
    name: string
    type: string
    size: number
    url: string
    resourceType: string
  }>({
    url: '/api/v1/files/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}
