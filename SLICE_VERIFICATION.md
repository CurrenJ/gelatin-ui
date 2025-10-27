# 9-Slice Verification

## Parameter Order
`drawTexture(texture, x, y, width, height, u, v, texWidth, texHeight, textureWidth, textureHeight)`
- x, y: destination position
- width, height: destination size
- u, v: source UV position
- texWidth, texHeight: source region size to sample
- textureWidth, textureHeight: total atlas size

## Expected Behavior for Each Slice

Given:
- Source: texW × texH at (u, v)
- Slices: left, right, top, bottom
- Center source: centerW = texW - left - right, centerH = texH - top - bottom
- Destination: width × height at (x, y)
- Destination center: destCenterW = width - left - right, destCenterH = height - top - bottom

### 1. Top-Left Corner
- Dest: left × top at (x, y)
- Source: left × top from (u, v)
- **No stretching**

### 2. Top Edge
- Dest: destCenterW × top at (x+left, y)
- Source: centerW × top from (u+left, v)
- **Horizontal stretch only**

### 3. Top-Right Corner
- Dest: right × top at (x+left+destCenterW, y)
- Source: right × top from (u+left+centerW, v)
- **No stretching**

### 4. Left Edge
- Dest: left × destCenterH at (x, y+top)
- Source: left × centerH from (u, v+top)
- **Vertical stretch only**

### 5. Center
- Dest: destCenterW × destCenterH at (x+left, y+top)
- Source: centerW × centerH from (u+left, v+top)
- **Both directions stretched**

### 6. Right Edge
- Dest: right × destCenterH at (x+left+destCenterW, y+top)
- Source: right × centerH from (u+left+centerW, v+top)
- **Vertical stretch only**

### 7. Bottom-Left Corner
- Dest: left × bottom at (x, y+top+destCenterH)
- Source: left × bottom from (u, v+top+centerH)
- **No stretching**

### 8. Bottom Edge
- Dest: destCenterW × bottom at (x+left, y+top+destCenterH)
- Source: centerW × bottom from (u+left, v+top+centerH)
- **Horizontal stretch only**

### 9. Bottom-Right Corner
- Dest: right × bottom at (x+left+destCenterW, y+top+destCenterH)
- Source: right × bottom from (u+left+centerW, v+top+centerH)
- **No stretching**

