import fs from 'node:fs'
import path from 'node:path'
import { defineConfig } from '@playwright/test'

function resolveAgentBrowserChrome() {
  const homeDir = process.env.HOME
  if (!homeDir) {
    return null
  }

  const browsersDir = path.join(homeDir, '.agent-browser', 'browsers')
  if (!fs.existsSync(browsersDir)) {
    return null
  }

  const chromeBuilds = fs.readdirSync(browsersDir, { withFileTypes: true })
    .filter(entry => entry.isDirectory() && entry.name.startsWith('chrome-'))
    .map(entry => entry.name)
    .sort()
    .reverse()

  for (const build of chromeBuilds) {
    const executablePath = path.join(
      browsersDir,
      build,
      'Google Chrome for Testing.app',
      'Contents',
      'MacOS',
      'Google Chrome for Testing',
    )

    if (fs.existsSync(executablePath)) {
      return executablePath
    }
  }

  return null
}

const chromeExecutablePath = process.env.PLAYWRIGHT_CHROME_EXECUTABLE_PATH || resolveAgentBrowserChrome()
const launchArgs = process.env.PLAYWRIGHT_NO_SANDBOX === '1' ? ['--no-sandbox'] : []

export default defineConfig({
  testDir: './e2e',
  fullyParallel: false,
  retries: 0,
  workers: 1,
  reporter: 'line',
  outputDir: './test-results',
  use: {
    baseURL: process.env.PLAYWRIGHT_BASE_URL || 'http://127.0.0.1:5173',
    headless: true,
    trace: 'retain-on-failure',
    ...(chromeExecutablePath
      ? {
          launchOptions: {
            executablePath: chromeExecutablePath,
            args: launchArgs,
          },
        }
      : {
          channel: 'chrome',
          launchOptions: {
            args: launchArgs,
          },
        }),
  },
})
