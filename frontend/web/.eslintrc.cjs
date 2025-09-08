module.exports = {
  root: true,
  env: { browser: true, es2020: true },
  extends: [
    'eslint:recommended',
    'plugin:@typescript-eslint/recommended',
    'plugin:react-hooks/recommended',
    'plugin:react/recommended',
    '@byodian/eslint-config-react'
  ],
  ignorePatterns: ['dist', '.eslintrc.cjs'],
  parser: '@typescript-eslint/parser',
  plugins: ['react-refresh'],
  rules: {
    'react-refresh/only-export-components': [
      'warn',
      { allowConstantExport: true },
    ],
    'react/prop-types': 'off',
    curly: ['error', 'all'],
    '@typescript-eslint/brace-style': ['error', '1tbs', { allowSingleLine: false }],
    "@typescript-eslint/consistent-type-imports": ["error", {
      "prefer": "type-imports",
      "disallowTypeAnnotations": true
    }],

    // 确保只导入类型时使用 import type
    "@typescript-eslint/no-import-type-side-effects": "error",
    "react-refresh/only-export-components": "off"
  },
}
