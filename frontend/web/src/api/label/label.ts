import { request } from '@/lib/http'
import type { LabelItem, LabelCreateRequest, LabelUpdateRequest, SelectItem } from '@/types/label'
import type { NotePageLoaderData, NotePageRequestParams } from '@/types/note'

export function getLabelsApi() {
  return request<LabelItem[]>({
    url: '/api/v1/labels',
    method: 'get',
  })
}

export function createLabelApi(data: LabelCreateRequest) {
  return request<string>({
    url: '/api/v1/labels',
    method: 'post',
    responseType: 'text',
    data,
  })
}

export function updateLabelApi(labelId: string, data: LabelUpdateRequest) {
  return request<void>({
    url: `/api/v1/labels/${labelId}`,
    method: 'patch',
    data,
  })
}

export function deleteLabelApi(labelId: string) {
  return request<void>({
    url: `/api/v1/labels/${labelId}`,
    method: 'delete',
  })
}

export function getLabelSelectItemsApi() {
  return request<SelectItem[]>({
    url: '/api/v1/labels/select',
    method: 'get',
  })
}

export function getNotesByLabelApi(labelId: string, params: NotePageRequestParams) {
  return request<NotePageLoaderData>({
    url: `/api/v1/labels/${labelId}/notes`,
    method: 'get',
    params,
  })
}
