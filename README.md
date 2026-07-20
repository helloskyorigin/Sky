# ThreatShield AI - Legal Website

This repository contains the production-ready legal website for the **ThreatShield AI** application, designed to be hosted directly on GitHub Pages.

## Features

- **Premium Design:** Clean, modern, glassmorphism UI reminiscent of top tech companies.
- **Pure Technologies:** Built entirely with pure HTML, CSS, and JavaScript. No external frameworks like Bootstrap or Tailwind.
- **Dark/Light Mode:** Full support for system-based theme detection and manual toggling via the navigation bar.
- **Responsive:** Mobile-first design that scales beautifully to desktop displays.
- **Google Play Compliant:** Contains privacy policies and terms of use structurally aligned with Google Play Developer Policies.

## Files Included

- `index.html` - Landing page.
- `privacy.html` - Privacy Policy page.
- `terms.html` - Terms of Use page.
- `disclaimer.html` - Security Disclaimer page.
- `styles.css` - All styling, variables, and responsive breakpoints.
- `script.js` - Theme management, mobile navigation, and active state logic.
- `logo.svg` - ThreatShield AI vector logo.

## Deployment to GitHub Pages

This website is ready for immediate deployment.

1. Create a new repository on GitHub (e.g., `threatshield-legal`).
2. Upload all the files from this directory to the root of the repository.
3. Go to the repository **Settings** > **Pages**.
4. Under "Build and deployment", set the **Source** to `Deploy from a branch`.
5. Under "Branch", select `main` (or `master`) and `/ (root)`.
6. Click **Save**.
7. Your website will be live at `https://[your-username].github.io/[repository-name]/` within a few minutes.

## Theme Customization

To adjust the primary brand color, open `styles.css` and modify the `--accent-color` and `--accent-hover` CSS variables in the `:root` pseudo-class.
