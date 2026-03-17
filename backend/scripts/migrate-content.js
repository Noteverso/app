#!/usr/bin/env node

/**
 * HTML to JSON Migration Script
 * 
 * This script converts existing HTML content in the database to ProseMirror JSON format.
 * It uses TipTap/ProseMirror to parse HTML and generate the corresponding JSON structure.
 */

const { Client } = require('pg');
const { generateHTML, generateJSON } = require('@tiptap/html');
const { Schema } = require('@tiptap/pm/model');
const StarterKit = require('@tiptap/starter-kit');
const Link = require('@tiptap/extension-link');
const Highlight = require('@tiptap/extension-highlight');
const Image = require('@tiptap/extension-image');
const CodeBlockLowlight = require('@tiptap/extension-code-block-lowlight');
const TextStyle = require('@tiptap/extension-text-style');
const TaskItem = require('@tiptap/extension-task-item');
const TaskList = require('@tiptap/extension-task-list');
const { Color } = require('@tiptap/extension-color');

// TipTap extensions (same as frontend)
const extensions = [
  StarterKit.configure({
    heading: { levels: [1, 2, 3] },
    codeBlock: false,
  }),
  Link,
  Highlight,
  Image,
  CodeBlockLowlight,
  TextStyle,
  Color,
  TaskList,
  TaskItem.configure({ nested: true }),
];

// Database configuration
const dbConfig = {
  host: process.env.DB_HOST || 'localhost',
  port: process.env.DB_PORT || 5432,
  database: process.env.DB_NAME || 'noteverso_dev',
  user: process.env.DB_USER || 'noteverso',
  password: process.env.DB_PASSWORD || 'noteverso',
};

class MigrationScript {
  constructor(options = {}) {
    this.dryRun = options.dryRun || false;
    this.batchSize = options.batchSize || 100;
    this.client = new Client(dbConfig);
    this.stats = {
      total: 0,
      processed: 0,
      successful: 0,
      failed: 0,
      errors: []
    };
  }

  async connect() {
    await this.client.connect();
    console.log('Connected to database');
  }

  async disconnect() {
    await this.client.end();
    console.log('Disconnected from database');
  }

  convertHtmlToJson(html) {
    try {
      if (!html || html.trim() === '') {
        return { type: 'doc', content: [] };
      }

      // Use TipTap to convert HTML to JSON
      const json = generateJSON(html, extensions);
      return json;
    } catch (error) {
      throw new Error(`HTML to JSON conversion failed: ${error.message}`);
    }
  }

  async getTotalNoteCount() {
    const result = await this.client.query(
      'SELECT COUNT(*) FROM noteverso_note WHERE content_json IS NULL AND content IS NOT NULL'
    );
    return parseInt(result.rows[0].count);
  }

  async getNoteBatch(offset) {
    const result = await this.client.query(
      `SELECT id, note_id, content 
       FROM noteverso_note 
       WHERE content_json IS NULL AND content IS NOT NULL 
       ORDER BY id 
       LIMIT $1 OFFSET $2`,
      [this.batchSize, offset]
    );
    return result.rows;
  }

  async updateNoteJson(noteId, contentJson) {
    if (this.dryRun) {
      console.log(`[DRY RUN] Would update note ${noteId} with JSON content`);
      return;
    }

    await this.client.query(
      'UPDATE noteverso_note SET content_json = $1 WHERE note_id = $2',
      [JSON.stringify(contentJson), noteId]
    );
  }

  async processBatch(notes) {
    for (const note of notes) {
      try {
        console.log(`Processing note ${note.note_id}...`);
        
        const contentJson = this.convertHtmlToJson(note.content);
        await this.updateNoteJson(note.note_id, contentJson);
        
        this.stats.successful++;
        console.log(`✓ Successfully converted note ${note.note_id}`);
        
      } catch (error) {
        this.stats.failed++;
        this.stats.errors.push({
          noteId: note.note_id,
          error: error.message,
          html: note.content?.substring(0, 100) + '...'
        });
        console.error(`✗ Failed to convert note ${note.note_id}: ${error.message}`);
      }
      
      this.stats.processed++;
    }
  }

  async run() {
    console.log('Starting HTML to JSON migration...');
    console.log(`Mode: ${this.dryRun ? 'DRY RUN' : 'LIVE'}`);
    console.log(`Batch size: ${this.batchSize}`);
    
    try {
      await this.connect();
      
      this.stats.total = await this.getTotalNoteCount();
      console.log(`Total notes to migrate: ${this.stats.total}`);
      
      if (this.stats.total === 0) {
        console.log('No notes need migration');
        return;
      }

      let offset = 0;
      while (offset < this.stats.total) {
        console.log(`\nProcessing batch ${Math.floor(offset / this.batchSize) + 1}...`);
        
        const notes = await this.getNoteBatch(offset);
        if (notes.length === 0) break;
        
        await this.processBatch(notes);
        offset += this.batchSize;
        
        // Progress update
        const progress = Math.round((this.stats.processed / this.stats.total) * 100);
        console.log(`Progress: ${this.stats.processed}/${this.stats.total} (${progress}%)`);
      }
      
    } finally {
      await this.disconnect();
    }
    
    this.printSummary();
  }

  printSummary() {
    console.log('\n=== Migration Summary ===');
    console.log(`Total notes: ${this.stats.total}`);
    console.log(`Processed: ${this.stats.processed}`);
    console.log(`Successful: ${this.stats.successful}`);
    console.log(`Failed: ${this.stats.failed}`);
    
    if (this.stats.errors.length > 0) {
      console.log('\n=== Errors ===');
      this.stats.errors.forEach((error, index) => {
        console.log(`${index + 1}. Note ${error.noteId}: ${error.error}`);
        console.log(`   HTML: ${error.html}`);
      });
    }
    
    if (this.dryRun) {
      console.log('\n⚠️  This was a dry run. No changes were made to the database.');
      console.log('Run with --live to apply changes.');
    } else {
      console.log('\n✅ Migration completed!');
    }
  }
}

// CLI interface
async function main() {
  const args = process.argv.slice(2);
  const dryRun = !args.includes('--live');
  const batchSize = parseInt(args.find(arg => arg.startsWith('--batch-size='))?.split('=')[1]) || 100;

  if (args.includes('--help')) {
    console.log(`
Usage: node migrate-content.js [options]

Options:
  --live              Run migration (default is dry run)
  --batch-size=N      Process N notes at a time (default: 100)
  --help              Show this help message

Environment Variables:
  DB_HOST             Database host (default: localhost)
  DB_PORT             Database port (default: 5432)
  DB_NAME             Database name (default: noteverso_dev)
  DB_USER             Database user (default: noteverso)
  DB_PASSWORD         Database password (default: noteverso)

Examples:
  node migrate-content.js                    # Dry run with default settings
  node migrate-content.js --live             # Run migration
  node migrate-content.js --batch-size=50    # Use smaller batch size
    `);
    return;
  }

  const migration = new MigrationScript({ dryRun, batchSize });
  
  try {
    await migration.run();
  } catch (error) {
    console.error('Migration failed:', error.message);
    process.exit(1);
  }
}

if (require.main === module) {
  main();
}

module.exports = { MigrationScript };
