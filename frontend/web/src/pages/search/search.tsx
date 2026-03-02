import { useState, useEffect, useCallback, useRef } from 'react'
import { useSearchParams } from 'react-router-dom'
import { searchNotesApi, type SearchNotesParams } from '@/api/note/note'
import type { FullNote } from '@/types/note'
import { NoteList } from '@/features/note'
import { SearchBar, type SearchParams } from '@/components/search-bar/search-bar'
import { useToast } from '@/components/ui/toast/use-toast'

export function SearchPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [notes, setNotes] = useState<FullNote[]>([])
  const [loading, setLoading] = useState(false)
  const [page, setPage] = useState(1)
  const [hasMore, setHasMore] = useState(false)
  const [currentSearch, setCurrentSearch] = useState<SearchParams>({})
  const { toast } = useToast()
  const observer = useRef<IntersectionObserver>()

  useEffect(() => {
    // Initialize search from URL params
    const keyword = searchParams.get('keyword') || undefined
    const labelIds = searchParams.get('labelIds')?.split(',').filter(Boolean) || undefined
    const status = searchParams.get('status') ? parseInt(searchParams.get('status')!) : undefined
    const sortBy = searchParams.get('sortBy') || 'addedAt'
    const sortOrder = searchParams.get('sortOrder') || 'desc'

    const initialSearch: SearchParams = { keyword, labelIds, status, sortBy, sortOrder }
    setCurrentSearch(initialSearch)
    performSearch(initialSearch, 1)
  }, [])

  const performSearch = async (params: SearchParams, pageIndex: number) => {
    setLoading(true)

    const searchParams: SearchNotesParams = {
      ...params,
      pageIndex,
      pageSize: 20,
    }

    const response = await searchNotesApi(searchParams)

    if (response.ok) {
      const data = response.data
      if (pageIndex === 1) {
        setNotes(data.records)
      } else {
        setNotes(prev => [...prev, ...data.records])
      }
      setHasMore(data.total > pageIndex * 20)
      setPage(pageIndex)
    } else {
      toast({ title: 'Search failed', variant: 'destructive' })
    }

    setLoading(false)
  }

  const handleSearch = (params: SearchParams) => {
    // Update URL params
    const urlParams = new URLSearchParams()
    if (params.keyword) urlParams.set('keyword', params.keyword)
    if (params.labelIds && params.labelIds.length > 0) urlParams.set('labelIds', params.labelIds.join(','))
    if (params.status) urlParams.set('status', params.status.toString())
    if (params.sortBy) urlParams.set('sortBy', params.sortBy)
    if (params.sortOrder) urlParams.set('sortOrder', params.sortOrder)
    setSearchParams(urlParams)

    setCurrentSearch(params)
    performSearch(params, 1)
  }

  const lastNoteRef = useCallback((node: HTMLElement | null) => {
    if (loading) return
    if (observer.current) observer.current.disconnect()

    observer.current = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting && hasMore) {
        performSearch(currentSearch, page + 1)
      }
    })

    if (node) observer.current.observe(node)
  }, [loading, hasMore, currentSearch, page])

  return (
    <div className="p-6">
      <div className="mb-6">
        <h1 className="text-2xl font-bold mb-4">Search Notes</h1>
        <SearchBar onSearch={handleSearch} />
      </div>

      {loading && page === 1 ? (
        <div className="text-center py-12 text-gray-500">Searching...</div>
      ) : notes.length === 0 ? (
        <div className="text-center py-12 text-gray-500">
          {currentSearch.keyword || currentSearch.labelIds?.length || currentSearch.status
            ? 'No notes found matching your search'
            : 'Enter a search term or apply filters to find notes'}
        </div>
      ) : (
        <>
          <div className="mb-4 text-sm text-gray-600">
            Found {notes.length} note{notes.length !== 1 ? 's' : ''}
          </div>
          <NoteList notes={notes} lastNoteElementRef={lastNoteRef} />
          {loading && page > 1 && (
            <div className="text-center py-4 text-gray-500">Loading more...</div>
          )}
        </>
      )}
    </div>
  )
}
