#!/usr/bin/env node

/**
 * Test script for HTML to JSON conversion
 */

const { generateJSON } = require('@tiptap/html');
const StarterKit = require('@tiptap/starter-kit');

// Simple test without all extensions to avoid dependency issues
const extensions = [StarterKit];

function testConversion() {
  const testCases = [
    {
      name: 'Simple paragraph',
      html: '<p>Hello World</p>',
    },
    {
      name: 'Heading and paragraph',
      html: '<h1>Title</h1><p>Content</p>',
    },
    {
      name: 'List',
      html: '<ul><li>Item 1</li><li>Item 2</li></ul>',
    },
    {
      name: 'Empty content',
      html: '',
    },
  ];

  console.log('Testing HTML to JSON conversion...\n');

  testCases.forEach((testCase, index) => {
    try {
      console.log(`${index + 1}. ${testCase.name}`);
      console.log(`   HTML: ${testCase.html || '(empty)'}`);
      
      const json = generateJSON(testCase.html, extensions);
      console.log(`   JSON: ${JSON.stringify(json)}`);
      console.log('   ✓ Success\n');
      
    } catch (error) {
      console.log(`   ✗ Error: ${error.message}\n`);
    }
  });
}

if (require.main === module) {
  testConversion();
}
