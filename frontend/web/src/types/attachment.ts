export interface AttachmentDTO {
  attachmentId: string
  name: string
  type: string
  size: number
  url: string
  resourceType: string
  addedAt: string
}

export interface AttachmentPageResult {
  records: AttachmentDTO[]
  total: number
  pageIndex: number
  pageSize: number
}
