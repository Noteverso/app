#!/usr/bin/env node

/**
 * Simple HTML to JSON Migration Script (Demo Version)
 * 
 * This is a simplified version that demonstrates the migration concept
 * without requiring all TipTap dependencies.
 */

const { Client } = require('pg');

// Simple HTML to JSON converter (demo implementation)
function convertHtmlToJson(html) {
  if (!html || html.trim() === '') {
    return { type: 'doc', content: [] };
  }

  // Simple conversion for demo purposes
  // In real implementation, this would use TipTap's generateJSON
  const content = [];
  
  if (html.includes('<h1>')) {
    const match = html.match(/<h1>(.*?)<\/h1>/);
    if (match) {
      content.push({
        type: 'heading',
        attrs: { level: 1 },
        content: [{ type: 'text', text: match[1] }]
      });
    }
  }
  
  if (html.includes('<p>')) {
    const matches = html.match(/<p>(.*?)<\/p>/g);
    if (matches) {
      matches.forEach(match => {
        const text = match.replace(/<\/?[^>]+(>|$)/g, ""); // Strip HTML tags
        content.push({
          type: 'paragraph',
          content: [{ type: 'text', text }]
        });
      });
    }
  }
  
  if (html.includes('<ul>')) {
    const listItems = html.match(/<li>(.*?)<\/li>/g);
    if (listItems) {
      const listContent = listItems.map(item => ({
        type: 'listItem',
        content: [{
          type: 'paragraph',
          content: [{ type: 'text', text: item.replace(/<\/?[^>]+(>|$)/g, "") }]
        }]
      }));
      
      content.push({
        type: 'bulletList',
        content: listContent
      });
    }
  }
  
  return { type: 'doc', content };
}

// Database configuration
const dbConfig = {
  host: 'localhost',
  port: 5432,
  database: 'noteverso_dev',
  user: 'noteverso',
  password: 'noteverso',
};

async function runMigration(dryRun = true) {
  const client = new Client(dbConfig);
  
  try {
    await client.connect();
    console.log('Connected to database');
    
    // Get notes that need migration
    const result = await client.query(
      'SELECT note_id, content FROM noteverso_note WHERE content_json IS NULL AND content IS NOT NULL'
    );
    
    console.log(`Found ${result.rows.length} notes to migrate`);
    
    for (const note of result.rows) {
      console.log(`\nProcessing note: ${note.note_id}`);
      console.log(`HTML: ${note.content}`);
      
      try {
        const contentJson = convertHtmlToJson(note.content);
        console.log(`JSON: ${JSON.stringify(contentJson, null, 2)}`);
        
        if (!dryRun) {
          await client.query(
            'UPDATE noteverso_note SET content_json = $1 WHERE note_id = $2',
            [JSON.stringify(contentJson), note.note_id]
          );
          console.log('✓ Updated in database');
        } else {
          console.log('✓ Would update in database (dry run)');
        }
        
      } catch (error) {
        console.error(`✗ Error converting note ${note.note_id}: ${error.message}`);
      }
    }
    
  } finally {
    await client.end();
    console.log('\nDisconnected from database');
  }
}

// Run as dry run by default
if (require.main === module) {
  const dryRun = !process.argv.includes('--live');
  console.log(`Running migration in ${dryRun ? 'DRY RUN' : 'LIVE'} mode\n`);
  
  runMigration(dryRun).catch(error => {
    console.error('Migration failed:', error.message);
    process.exit(1);
  });
}

module.exports = { convertHtmlToJson, runMigration };
