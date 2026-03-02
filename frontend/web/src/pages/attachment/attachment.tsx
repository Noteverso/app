import { useEffect, useState, useCallback, useRef } from 'react'
import { Download, Trash2, FileIcon, ImageIcon } from 'lucide-react'
import { getUserAttachmentsApi, getAttachmentUrlApi, deleteAttachmentApi } from '@/api/attachment/attachment'
import type { AttachmentDTO } from '@/types/attachment'
import { Button } from '@/components/ui/button/button'
import { useToast } from '@/components/ui/toast/use-toast'
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from '@/components/ui/alert-dialog/alert-dialog'

export function Attachment() {
  const [attachments, setAttachments] = useState<AttachmentDTO[]>([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(1)
  const [hasMore, setHasMore] = useState(true)
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [deletingAttachment, setDeletingAttachment] = useState<AttachmentDTO | null>(null)
  const { toast } = useToast()
  const observer = useRef<IntersectionObserver>()

  useEffect(() => {
    loadAttachments(1)
  }, [])

  const loadAttachments = async (pageIndex: number) => {
    setLoading(true)
    const response = await getUserAttachmentsApi(pageIndex, 20)
    
    if (response.ok) {
      const data = response.data
      if (pageIndex === 1) {
        setAttachments(data.records)
      } else {
        setAttachments(prev => [...prev, ...data.records])
      }
      setHasMore(data.total > pageIndex * 20)
    } else {
      toast({ title: 'Failed to load attachments', variant: 'destructive' })
    }
    setLoading(false)
  }

  const lastAttachmentRef = useCallback((node: HTMLElement | null) => {
    if (loading) return
    if (observer.current) observer.current.disconnect()

    observer.current = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting && hasMore) {
        setPage(prev => {
          const nextPage = prev + 1
          loadAttachments(nextPage)
          return nextPage
        })
      }
    })

    if (node) observer.current.observe(node)
  }, [loading, hasMore])

  const handleDownload = async (attachment: AttachmentDTO) => {
    const response = await getAttachmentUrlApi(attachment.attachmentId)
    if (response.ok) {
      window.open(response.data, '_blank')
    } else {
      toast({ title: 'Failed to get download URL', variant: 'destructive' })
    }
  }

  const handleDelete = async () => {
    if (!deletingAttachment) return

    const response = await deleteAttachmentApi(deletingAttachment.attachmentId)
    
    if (response.ok) {
      toast({ title: 'Attachment deleted successfully' })
      setAttachments(prev => prev.filter(a => a.attachmentId !== deletingAttachment.attachmentId))
      setDeleteDialogOpen(false)
      setDeletingAttachment(null)
    } else {
      toast({ title: 'Failed to delete attachment', variant: 'destructive' })
    }
  }

  const formatFileSize = (bytes: number) => {
    if (bytes < 1024) return bytes + ' B'
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
  }

  const isImage = (type: string) => type.startsWith('image/')

  if (loading && page === 1) return <div className="p-6">Loading...</div>

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-6">Attachments</h1>

      {attachments.length === 0 ? (
        <div className="text-center text-gray-500 py-12">No attachments yet</div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
          {attachments.map((attachment, index) => (
            <div
              key={attachment.attachmentId}
              ref={index === attachments.length - 1 ? lastAttachmentRef : null}
              className="border rounded-lg p-4 hover:shadow-md transition-shadow"
            >
              <div className="flex items-start justify-between mb-3">
                <div className="flex-1 min-w-0">
                  {isImage(attachment.type) ? (
                    <ImageIcon className="w-8 h-8 text-blue-500 mb-2" />
                  ) : (
                    <FileIcon className="w-8 h-8 text-gray-500 mb-2" />
                  )}
                  <div className="font-medium truncate" title={attachment.name}>
                    {attachment.name}
                  </div>
                  <div className="text-sm text-gray-500">
                    {formatFileSize(attachment.size)}
                  </div>
                  <div className="text-xs text-gray-400 mt-1">
                    {new Date(attachment.addedAt).toLocaleDateString()}
                  </div>
                </div>
              </div>
              <div className="flex gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  className="flex-1"
                  onClick={() => handleDownload(attachment)}
                >
                  <Download className="w-4 h-4 mr-1" />
                  Download
                </Button>
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => {
                    setDeletingAttachment(attachment)
                    setDeleteDialogOpen(true)
                  }}
                >
                  <Trash2 className="w-4 h-4" />
                </Button>
              </div>
            </div>
          ))}
        </div>
      )}

      {loading && page > 1 && (
        <div className="text-center py-4 text-gray-500">Loading more...</div>
      )}

      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Delete Attachment</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to delete "{deletingAttachment?.name}"? This action cannot be undone.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction onClick={handleDelete}>Delete</AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
