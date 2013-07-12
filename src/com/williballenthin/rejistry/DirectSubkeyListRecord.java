package com.williballenthin.rejistry;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DirectSubkeyListRecord extends SubkeyListRecord {
    private final int _item_size;

    /**
     *
     * @param buf
     * @param offset
     */
    public DirectSubkeyListRecord(ByteBuffer buf, int offset, int item_size) throws RegistryParseException {
        super(buf, offset);
        this._item_size = item_size;
    }

    public Iterator<NKRecord> getSubkeys() {
        return new Iterator<NKRecord>() {
            private int _index = 0;
            private int _max_index = DirectSubkeyListRecord.this.getSubkeyCount();
            private NKRecord _next = null;

            @Override
            public boolean hasNext() {
                if (this._index + 1 > this._max_index) {
                    return false;
                }

                int offset = DirectSubkeyListRecord.this.getDword(this._index * DirectSubkeyListRecord.this._item_size);
                int parent_offset = REGFHeader.FIRST_HBIN_OFFSET + offset;
                Cell c = new Cell(DirectSubkeyListRecord.this._buf, parent_offset);
                try {
                    this._next = c.getNKRecord();
                } catch (RegistryParseException e) {
                    return false;
                }

                return true;
            }

            @Override
            public NKRecord next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException("No more subkeys in the subkey list");
                }
                this._index++;
                return this._next;
            }

            @Override
            public void remove() {
                // TODO(wb): implement me
                throw new UnsupportedOperationException("Remove operation not supported for subkey lists");
            }
        };
    }
}