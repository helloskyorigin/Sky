const fs = require('fs');
let svg = fs.readFileSync('logo_adaptive_foreground.svg', 'utf8');
// Remove all <g...>
svg = svg.replace(/<g transform="translate\(170, 170\) scale\(0.66\)">/g, '');
svg = svg.replace(/<\/g><\/svg>/g, '</svg>');

// Add a single <g> right after </defs>
svg = svg.replace('</defs>', '</defs><g transform="translate(170, 170) scale(0.66)">');
svg = svg.replace('</svg>', '</g></svg>');

fs.writeFileSync('logo_adaptive_foreground.svg', svg);
