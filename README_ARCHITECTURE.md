# Solution

## Excel file handling
- Tech Used:
  - <strong>Apache POI</strong>: A Java API for Microsoft Documents (Excel)
  - File format: OOXML as it's Microsoft Excel (2007+) file with extension <strong>.xlsx</strong>
- Data extracting:
    - The data was only on <strong>two columns [A,B]</strong> inside only one sheet.
    - Thus service was coded as data was present.

E.g.:

|   A    |     B    |
|--------|----------|
| ESTADO | CIDADE   |
| SP     | BAURU    |
| SP     | BOTUCATU |
| SP     | FARTURA  |
| PR     | MARINGA  |

- PS: This solution could be improved to allow a more generic solution if needed.