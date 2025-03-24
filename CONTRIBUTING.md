# Contributing to CertVault

## Introduction

### Thank you for considering contributing!
Thank you for your interest in contributing to CertVault. Your contributions help make this project better for everyone.

> "Following these guidelines ensures we respect the time of maintainers and contributors. In return, we commit to addressing issues and reviewing contributions promptly."

### Why these guidelines?
These guidelines help maintain project quality and ensure contributions align with CertVault's goals. They reduce friction in collaboration and help maintainers focus on what matters most.

### What we're looking for
- Bug fixes
- Documentation improvements
- Feature enhancements aligned with project goals
- Code optimizations
- Security improvements

> "Even small contributions like typo fixes or documentation updates are valuable. Every contribution helps!"

### What we're not looking for
[//]: # (- Support requests &#40;use our [GitHub Discussions]&#40;https://github.com/gregperlinli/certvault/discussions&#41; instead&#41;)
- Duplicate feature requests without prior discussion
- Unscoped large-scale changes without proposal

---

## Ground Rules

### Behavior expectations
- Be respectful and considerate in all interactions
- Follow the [CertVault Code of Conduct](CODE_OF_CONDUCT.md)
- Keep discussions constructive and solution-focused

### Technical requirements
- All contributions must include tests
- Maintain code style consistency (see `pom.xml` for Java conventions)
- Ensure cross-platform compatibility (JVM 17+, Linux/macOS/Windows)
- Document new features in `README.md` or `docs/`

---

## Your First Contribution

### Where to start
Check these issue labels:
- **good first issue**: Simple tasks for new contributors
- **help wanted**: Issues needing attention
- **documentation**: Documentation improvements

> "New to open source? Start with a 'good first issue'! Our community is here to help."

### Learning resources
- [How to Contribute to Open Source](https://opensource.guide/how-to-contribute/)
- [First Timers Only Guide](https://www.firsttimersonly.com/)

---

## Getting Started

### Contribution workflow
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Make your changes
4. Run tests: `./mvnw test`
5. Submit a pull request

### Legal requirements
- Sign our [Contributor License Agreement](CLA.md) if contributing significant code
- Small fixes (typos, formatting) don't require CLA

---

## Reporting Bugs

### Security vulnerabilities
**DO NOT OPEN ISSUES FOR SECURITY ISSUES**  
Email: lihaolin13@outlook.com

### Bug report guidelines
Include the following in all bug reports:
1. CertVault version (`git rev-parse HEAD`)
2. Java version (`java -version`)
3. Operating system and architecture
4. Steps to reproduce
5. Expected vs actual behavior
6. Screenshots/error logs (if applicable)

---

## Suggesting Features

### Project philosophy
CertVault focuses on:
- Secure certificate management
- Easy integration with Java applications
- Compliance with modern security standards

### Feature proposal process
1. Search existing issues to avoid duplicates
2. Create a proposal issue with:
   - Use case description
   - Proposed implementation
   - Security/compatibility impact
3. Wait for feedback from maintainers
4. Implement only after proposal is approved