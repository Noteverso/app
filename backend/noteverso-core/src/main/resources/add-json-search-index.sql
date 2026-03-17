-- Add GIN index for JSON content search
-- This index enables efficient full-text search on JSON content

-- Create GIN index for JSON text extraction and search
CREATE INDEX IF NOT EXISTS idx_note_content_json_text 
ON noteverso_note 
USING GIN((content_json #>> '{}'));

-- Add comment explaining the index
COMMENT ON INDEX idx_note_content_json_text IS 'GIN index for full-text search on JSON content using text extraction';
