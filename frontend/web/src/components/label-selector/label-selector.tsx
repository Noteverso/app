import { useState, useEffect } from 'react'
import { Check } from 'lucide-react'
import { getLabelSelectItemsApi } from '@/api/label/label'
import type { SelectItem } from '@/types/label'
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover/popover'
import { Button } from '@/components/ui/button/button'
import { Badge } from '@/components/ui/badge/badge'

interface LabelSelectorProps {
  selectedLabelIds: string[]
  onChange: (labelIds: string[]) => void
}

export function LabelSelector({ selectedLabelIds, onChange }: LabelSelectorProps) {
  const [labels, setLabels] = useState<SelectItem[]>([])
  const [open, setOpen] = useState(false)

  useEffect(() => {
    loadLabels()
  }, [])

  const loadLabels = async () => {
    const response = await getLabelSelectItemsApi()
    if (response.ok) {
      setLabels(response.data)
    }
  }

  const toggleLabel = (labelId: string) => {
    if (selectedLabelIds.includes(labelId)) {
      onChange(selectedLabelIds.filter(id => id !== labelId))
    } else {
      onChange([...selectedLabelIds, labelId])
    }
  }

  const selectedLabels = labels.filter(label => selectedLabelIds.includes(label.value))

  return (
    <div className="flex items-center gap-2">
      <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
          <Button variant="outline" size="sm">
            Add Label
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-64">
          <div className="space-y-2">
            {labels.map((label) => (
              <button
                key={label.value}
                className="w-full flex items-center justify-between p-2 hover:bg-gray-100 rounded"
                onClick={() => toggleLabel(label.value)}
              >
                <div className="flex items-center gap-2">
                  <div className="w-3 h-3 rounded-full" style={{ backgroundColor: label.color }} />
                  <span className="text-sm">{label.name}</span>
                </div>
                {selectedLabelIds.includes(label.value) && <Check className="w-4 h-4" />}
              </button>
            ))}
          </div>
        </PopoverContent>
      </Popover>
      
      <div className="flex gap-1 flex-wrap">
        {selectedLabels.map((label) => (
          <Badge
            key={label.value}
            style={{ backgroundColor: label.color }}
            className="text-white"
          >
            {label.name}
          </Badge>
        ))}
      </div>
    </div>
  )
}
