# Noteverso app

## Initial Spec Kit setup

1. [Install Specify CLI](https://github.com/github/spec-kit?tab=readme-ov-file#1-install-specify-cli)
2. Install AI CLI tools, eg Github Copilot or Anthropic Claude
    ```bash
    winget install GitHub.Copilot # windows
    brew install copilot-cli      # macOS
    ```
3. Run `spec kit init` in the root of your project
    ```bash
    # Create new project
    specify init <PROJECT_NAME>

    # Or initialize in existing project
    # AI assistant: copilot, claude, gemini, cursor-agent, codex
    specify init . --ai claude
    # or
    specify init --here --ai claude

    # Check installed tools
    specify check
    ```
