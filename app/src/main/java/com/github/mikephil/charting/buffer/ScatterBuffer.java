
package com.github.mikephil.charting.buffer;

import com.github.mikephil.charting.data.Entry;

public class ScatterBuffer extends AbstractBuffer<Entry> {
    
    public ScatterBuffer(int size) {
        super(size);
    }

    protected void addForm(float x, float y) {
        buffer[index++] = x;
        buffer[index++] = y;
    }

    @Override
    public void feed(java.util.List<Entry> entries) {
        
        float size = entries.size() * phaseX;
        
        for (int i = 0; i < size; i++) {

            Entry e = entries.get(i);
            addForm(e.getXIndex(), e.getVal() * phaseY);
        }
        
        reset();
    }
}
