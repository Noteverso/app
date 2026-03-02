import { json, type LoaderFunctionArgs } from 'react-router-dom'
import { getNotesByLabelApi, getLabelsApi } from '@/api/label/label'

export async function labelDetailLoader({ params }: LoaderFunctionArgs) {
  const { labelId } = params
  
  if (!labelId) {
    throw json({ message: 'Label ID is required' }, { status: 400 })
  }

  const [notesResponse, labelsResponse] = await Promise.all([
    getNotesByLabelApi(labelId, { pageSize: 10, pageIndex: 1 }),
    getLabelsApi(),
  ])

  if (!notesResponse.ok) {
    throw json(notesResponse.data, { status: notesResponse.status })
  }

  const label = labelsResponse.ok 
    ? labelsResponse.data.find(l => l.labelId === labelId)
    : null

  return {
    notePageData: notesResponse.data,
    labelName: label?.name || 'Label',
  }
}
