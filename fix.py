def fix_history():
    with open('app/src/main/java/com/example/HistoryScreen.kt', 'r') as f:
        c = f.read()
    c = c.replace('import androidx.compose.ui.Modifier\n', 'import androidx.compose.ui.Modifier\nimport androidx.compose.ui.draw.clip\n')
    c = c.replace('''                        )
                    }
                    )''', '''                        )
                    )
                }''')
    with open('app/src/main/java/com/example/HistoryScreen.kt', 'w') as f:
        f.write(c)

def fix_daily():
    with open('app/src/main/java/com/example/DailyChallengeScreen.kt', 'r') as f:
        c = f.read()
    c = c.replace('import androidx.compose.ui.Modifier\n', 'import androidx.compose.ui.Modifier\nimport androidx.compose.ui.draw.clip\n')
    c = c.replace('''                        },
                    },''', '''                        ),''')
    
    # We also need to add the closing bracket for Row
    c = c.replace('''                        )
                    )
                },
                navigationIcon = {''', '''                        )
                    )
                }
                },
                navigationIcon = {''')
    with open('app/src/main/java/com/example/DailyChallengeScreen.kt', 'w') as f:
        f.write(c)

def fix_analysis():
    with open('app/src/main/java/com/example/AnalysisResultScreen.kt', 'r') as f:
        c = f.read()
    c = c.replace('import androidx.compose.ui.Modifier\n', 'import androidx.compose.ui.Modifier\nimport androidx.compose.ui.draw.clip\n')
    c = c.replace('''                        )
                    }
                    )''', '''                        )
                    )
                }''')
    with open('app/src/main/java/com/example/AnalysisResultScreen.kt', 'w') as f:
        f.write(c)

def fix_home():
    with open('app/src/main/java/com/example/HomeScreen.kt', 'r') as f:
        c = f.read()
    c = c.replace('import androidx.compose.ui.Modifier\n', 'import androidx.compose.ui.Modifier\nimport androidx.compose.ui.draw.clip\n')
    with open('app/src/main/java/com/example/HomeScreen.kt', 'w') as f:
        f.write(c)

def fix_settings():
    with open('app/src/main/java/com/example/SettingsScreen.kt', 'r') as f:
        c = f.read()
    c = c.replace('import androidx.compose.ui.Modifier\n', 'import androidx.compose.ui.Modifier\nimport androidx.compose.ui.draw.clip\nimport androidx.compose.ui.draw.drawBehind\n')
    with open('app/src/main/java/com/example/SettingsScreen.kt', 'w') as f:
        f.write(c)

def fix_about():
    with open('app/src/main/java/com/example/AboutScreen.kt', 'r') as f:
        c = f.read()
    c = c.replace('import androidx.compose.ui.Modifier\n', 'import androidx.compose.ui.Modifier\nimport androidx.compose.ui.draw.clip\nimport androidx.compose.ui.draw.drawBehind\n')
    with open('app/src/main/java/com/example/AboutScreen.kt', 'w') as f:
        f.write(c)

def fix_webview():
    with open('app/src/main/java/com/example/AppWebViewScreen.kt', 'r') as f:
        c = f.read()
    c = c.replace('import androidx.compose.ui.Modifier\n', 'import androidx.compose.ui.Modifier\nimport androidx.compose.ui.draw.clip\nimport androidx.compose.foundation.layout.Spacer\nimport androidx.compose.foundation.layout.Row\nimport androidx.compose.foundation.layout.width\nimport androidx.compose.foundation.layout.size\nimport androidx.compose.ui.unit.dp\n')
    with open('app/src/main/java/com/example/AppWebViewScreen.kt', 'w') as f:
        f.write(c)

fix_history()
fix_daily()
fix_analysis()
fix_home()
fix_settings()
fix_about()
fix_webview()
