import { useState, useEffect } from 'react'
import { Search, X, Filter } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { Input } from '@/components/ui/input/input'
import { Button } from '@/components/ui/button/button'
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover/popover'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select/select'
import { getLabelSelectItemsApi } from '@/api/label/label'
import type { SelectItem as LabelSelectItem } from '@/types/label'
import { Checkbox } from '@/components/ui/checkbox/checkbox'

interface SearchBarProps {
  onSearch: (params: SearchParams) => void
}

export interface SearchParams {
  keyword?: string
  labelIds?: string[]
  status?: number
  sortBy?: string
  sortOrder?: string
}

export function SearchBar({ onSearch }: SearchBarProps) {
  const [keyword, setKeyword] = useState('')
  const [labels, setLabels] = useState<LabelSelectItem[]>([])
  const [selectedLabelIds, setSelectedLabelIds] = useState<string[]>([])
  const [status, setStatus] = useState<number | undefined>()
  const [sortBy, setSortBy] = useState('addedAt')
  const [sortOrder, setSortOrder] = useState('desc')
  const [filterOpen, setFilterOpen] = useState(false)

  useEffect(() => {
    loadLabels()
  }, [])

  const loadLabels = async () => {
    const response = await getLabelSelectItemsApi()
    if (response.ok) {
      setLabels(response.data)
    }
  }

  const handleSearch = () => {
    const params: SearchParams = {}
    if (keyword.trim()) params.keyword = keyword.trim()
    if (selectedLabelIds.length > 0) params.labelIds = selectedLabelIds
    if (status) params.status = status
    params.sortBy = sortBy
    params.sortOrder = sortOrder
    onSearch(params)
  }

  const handleClear = () => {
    setKeyword('')
    setSelectedLabelIds([])
    setStatus(undefined)
    setSortBy('addedAt')
    setSortOrder('desc')
    onSearch({})
  }

  const toggleLabel = (labelId: string) => {
    setSelectedLabelIds(prev =>
      prev.includes(labelId)
        ? prev.filter(id => id !== labelId)
        : [...prev, labelId]
    )
  }

  const hasFilters = keyword || selectedLabelIds.length > 0 || status

  return (
    <div className="flex items-center gap-2 flex-1 max-w-2xl">
      <div className="relative flex-1">
        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
        <Input
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
          placeholder="Search notes..."
          className="pl-10 pr-10"
        />
        {hasFilters && (
          <button
            onClick={handleClear}
            className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
          >
            <X className="w-4 h-4" />
          </button>
        )}
      </div>

      <Popover open={filterOpen} onOpenChange={setFilterOpen}>
        <PopoverTrigger asChild>
          <Button variant="outline" size="sm">
            <Filter className="w-4 h-4 mr-2" />
            Filters
            {(selectedLabelIds.length > 0 || status) && (
              <span className="ml-2 bg-blue-500 text-white rounded-full w-5 h-5 text-xs flex items-center justify-center">
                {(selectedLabelIds.length > 0 ? 1 : 0) + (status ? 1 : 0)}
              </span>
            )}
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-80">
          <div className="space-y-4">
            <div>
              <label className="text-sm font-medium mb-2 block">Labels</label>
              <div className="space-y-2 max-h-40 overflow-y-auto">
                {labels.map((label) => (
                  <div key={label.value} className="flex items-center gap-2">
                    <Checkbox
                      checked={selectedLabelIds.includes(label.value)}
                      onCheckedChange={() => toggleLabel(label.value)}
                    />
                    <div className="w-3 h-3 rounded-full" style={{ backgroundColor: label.color }} />
                    <span className="text-sm">{label.name}</span>
                  </div>
                ))}
              </div>
            </div>

            <div>
              <label className="text-sm font-medium mb-2 block">Status</label>
              <Select value={status?.toString()} onValueChange={(v) => setStatus(v ? parseInt(v) : undefined)}>
                <SelectTrigger>
                  <SelectValue placeholder="All notes" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="0">All notes</SelectItem>
                  <SelectItem value="1">Pinned</SelectItem>
                  <SelectItem value="2">Archived</SelectItem>
                  <SelectItem value="3">Favorite</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div>
              <label className="text-sm font-medium mb-2 block">Sort by</label>
              <div className="flex gap-2">
                <Select value={sortBy} onValueChange={setSortBy}>
                  <SelectTrigger className="flex-1">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="addedAt">Created</SelectItem>
                    <SelectItem value="updatedAt">Updated</SelectItem>
                  </SelectContent>
                </Select>
                <Select value={sortOrder} onValueChange={setSortOrder}>
                  <SelectTrigger className="w-24">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="desc">Desc</SelectItem>
                    <SelectItem value="asc">Asc</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>

            <Button onClick={handleSearch} className="w-full">
              Apply Filters
            </Button>
          </div>
        </PopoverContent>
      </Popover>

      <Button onClick={handleSearch} size="sm">
        Search
      </Button>
    </div>
  )
}
