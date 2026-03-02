export interface LabelItem {
  labelId: string
  name: string
  color: string
  noteCount?: number
}

export interface LabelCreateRequest {
  name: string
  color: string
  isFavorite?: number
}

export interface LabelUpdateRequest {
  name: string
  color: string
  isFavorite?: number
}

export interface SelectItem {
  name: string
  value: string
  color?: string
}
