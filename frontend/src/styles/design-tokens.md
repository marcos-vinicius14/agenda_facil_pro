#agenda_facil_pro

# Agenda Fácil Pro - Design System (Codenamed: Pulse)

## 1. Design Principles

1. **Clinical Clarity:** The software should appear clean and sterile. Plenty of white space.

2. **Optional High Density:** Secretaries need to see many appointments on the screen. The design should support dense tables without visual clutter.

3. **Immediate Feedback:** Every action (save, schedule, error) should have clear visual feedback (semantic colors).

## 2. Color Palette

### Primary (The Brand)
We will use a "HealthTech" Blue that conveys trust and technology.

- **Brand Primary:** `#2563EB` (Royal Blue) - Main buttons, Links, Highlights.

- **Brand Light:** `#EFF6FF` (Blue 50) - Active card backgrounds, hovers.

- **Brand Dark:** `#1E40AF` (Blue 800) - Text on the primary background.

### Neutrals (Structure)

Based on the "Slate" scale (bluish gray) to avoid absolute black which tires the eyes.

- **Background App:** `#F8FAFC` (Slate 50) - General application background.

- **Surface White:** `#FFFFFF` - Cards, Modals, Sidebar.

- **Text Main:** `#0F172A` (Slate 900) - Titles, Main text.

- **Text Secondary:** `#64748B` (Slate 500) - Legends, Labels, Placeholders.

- **Border:** `#E2E8F0` (Slate 200) - Subtle dividers.

### Semantic (Appointment Status)

Crucial for the visual agenda.

- **Success (Confirmed/Paid):** `#10B981` (Emerald)
- **Warning (Pending/Awaiting):** `#F59E0B` (Amber)
- **Error (Cancelled/Overdue):** `#EF4444` (Red)
- **Info (In progress):** `#0EA5E9` (Sky)

## 3. Typography

**Family:** `Inter` (Google Fonts)

| | | | | |
| ---------- | -------------- | ---------------- | --------------- | -------------------- |
| **Style** | **Weight** | **Size (px)** | **Line Height** | **Usage** |
| **H1** | Bold (700) | 24px | 32px | Page Titles |
| **H2** | SemiBold (600) | 20px | 28px | Section Subtitles |
| **H3** | Medium (500) | 16px | 24px | Card Titles |
| **Body** | Regular (400) | 14px | 20px | General Text, Tables |
| **Small** | Regular (400) | 12px | 16px | Captions, Metadata |

| **Button** | Medium (500) | 14px | 20px | Buttons |

## 4. Basic Components

### Buttons

- **Primary:** Blue Background (#2563EB), White Text. Radius 8px. Soft Shadow. - **Secondary:** White background, Slate 200 border, Slate 700 text.

- **Ghost:** No background, Slate 600 text. Light gray hover. (Used for action icons in the table).

- **Destructive:** Light red background, red text. (Ex: Delete Patient).

### Inputs (Forms)

- Height: 40px (Comfortable for mouse and touch).

- Border: Slate 300.
- Focus: 2px blue ring (#2563EB).

- Label: Slate 700, 14px size, above the input.

### Cards

- White background.

- Border: 1px solid Slate 200.

- Shadow: `shadow-sm` (subtle).

- Padding: 24px (Desktop), 16px (Mobile).

### Sidebar (Navigation)

- Color: Slate 900 (Dark Mode for contrast with the content) OR White with a right border.

- _Recommendation:_ Clean white sidebar for a more clinical look.

- Active Item: Light Blue Background (#EFF6FF), Blue Text, Blue Left Border.

## 5. Icons

Use Lucide React.

These are open-source icons, thin lines, very consistent and professional.

Examples: Calendar, Users, DollarSign, Settings, Activity.