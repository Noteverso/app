import { useState, useEffect, useCallback, useMemo, useRef } from 'react'
import { useSearchParams } from 'react-router-dom'
import { searchNotesApi, type SearchNotesParams } from '@/api/note/note'
import type { FullNote } from '@/types/note'
import { NoteList } from '@/features/note'
import { SearchBar, type SearchParams } from '@/components/search-bar/search-bar'
import { useToast } from '@/components/ui/toast/use-toast'

const PAGE_SIZE = 20

function parseSearchFromUrl(searchParams: URLSearchParams): SearchParams {
  return {
    keyword: searchParams.get('keyword') || undefined,
    labelIds: searchParams.get('labelIds')?.split(',').filter(Boolean) || undefined,
    status: searchParams.get('status') ? parseInt(searchParams.get('status')!, 10) : undefined,
    sortBy: searchParams.get('sortBy') || 'addedAt',
    sortOrder: searchParams.get('sortOrder') || 'desc',
  }
}

function buildUrlSearchParams(params: SearchParams) {
  const nextSearchParams = new URLSearchParams()

  if (params.keyword) nextSearchParams.set('keyword', params.keyword)
  if (params.labelIds && params.labelIds.length > 0) nextSearchParams.set('labelIds', params.labelIds.join(','))
  if (params.status !== undefined) nextSearchParams.set('status', params.status.toString())
  if (params.sortBy) nextSearchParams.set('sortBy', params.sortBy)
  if (params.sortOrder) nextSearchParams.set('sortOrder', params.sortOrder)

  return nextSearchParams
}

function getSearchSignature(params: SearchParams) {
  return JSON.stringify({
    keyword: params.keyword ?? '',
    labelIds: params.labelIds ?? [],
    status: params.status ?? null,
    sortBy: params.sortBy ?? 'addedAt',
    sortOrder: params.sortOrder ?? 'desc',
  })
}

export function SearchPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [notes, setNotes] = useState<FullNote[]>([])
  const [loading, setLoading] = useState(false)
  const [page, setPage] = useState(1)
  const [hasMore, setHasMore] = useState(false)
  const [currentSearch, setCurrentSearch] = useState<SearchParams>(() => parseSearchFromUrl(searchParams))
  const { toast } = useToast()
  const observer = useRef<IntersectionObserver>()
  const requestIdRef = useRef(0)
  const loadingRef = useRef(false)
  const hasMoreRef = useRef(hasMore)
  const currentSearchSignature = useMemo(() => getSearchSignature(currentSearch), [currentSearch])

  useEffect(() => {
    hasMoreRef.current = hasMore
  }, [hasMore])

  useEffect(() => {
    const nextSearch = parseSearchFromUrl(searchParams)
    const nextSignature = getSearchSignature(nextSearch)

    if (nextSignature === currentSearchSignature) {
      return
    }

    if (observer.current) {
      observer.current.disconnect()
    }

    loadingRef.current = true
    setHasMore(false)
    setPage(1)
    setCurrentSearch(nextSearch)
  }, [currentSearchSignature, searchParams])

  useEffect(() => {
    let isActive = true
    const currentRequestId = ++requestIdRef.current

    loadingRef.current = true
    setLoading(true)

    const runSearch = async () => {
      try {
        const searchRequest: SearchNotesParams = {
          ...currentSearch,
          pageIndex: page,
          pageSize: PAGE_SIZE,
        }

        const response = await searchNotesApi(searchRequest)

        if (!isActive || currentRequestId !== requestIdRef.current) {
          return
        }

        if (response.ok) {
          const data = response.data
          if (page === 1) {
            setNotes(data.records)
          } else {
            setNotes(prevNotes => [...prevNotes, ...data.records])
          }

          setHasMore(data.total > page * PAGE_SIZE)
        } else {
          toast({ title: 'Search failed', variant: 'destructive' })
        }
      } catch {
        if (!isActive || currentRequestId !== requestIdRef.current) {
          return
        }

        toast({ title: 'Search failed', variant: 'destructive' })
      }
    }

    runSearch().finally(() => {
      if (!isActive || currentRequestId !== requestIdRef.current) {
        return
      }

      loadingRef.current = false
      setLoading(false)
    })

    return () => {
      isActive = false
    }
  }, [currentSearch, page, toast])

  const handleSearch = (params: SearchParams) => {
    if (observer.current) {
      observer.current.disconnect()
    }

    loadingRef.current = true
    setHasMore(false)
    setPage(1)
    setCurrentSearch(params)
    setSearchParams(buildUrlSearchParams(params), { preventScrollReset: true })
  }

  const lastNoteRef = useCallback((node: HTMLElement | null) => {
    if (observer.current) {
      observer.current.disconnect()
    }

    if (loading || loadingRef.current || !hasMore) {
      return
    }

    observer.current = new IntersectionObserver((entries) => {
      if (!entries[0]?.isIntersecting || loadingRef.current || !hasMoreRef.current) {
        return
      }

      loadingRef.current = true
      setPage(currentPage => currentPage + 1)
    })

    if (node) {
      observer.current.observe(node)
    }
  }, [hasMore, loading])

  useEffect(() => () => {
    observer.current?.disconnect()
  }, [])

  return (
    <div className="px-6 pb-6">
      <div
        data-testid="search-toolbar"
        className="sticky top-0 z-10 -mx-6 mb-6 border-b bg-white px-6 pb-4 pt-6 shadow-sm"
      >
        <h1 className="mb-4 text-2xl font-bold">Search Notes</h1>
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
          <NoteList notes={notes} refFunc={lastNoteRef} />
          {loading && page > 1 && (
            <div className="text-center py-4 text-gray-500">Loading more...</div>
          )}
        </>
      )}
    </div>
  )
}
