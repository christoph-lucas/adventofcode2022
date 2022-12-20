package ch.lucas.y2022d20;

public class DoubleLinkedListNode {

    public boolean mixed;
    public DoubleLinkedListNode prev;
    public DoubleLinkedListNode next;
    public long val;

    public DoubleLinkedListNode(long val) {
        this.val = val;
    }

    public boolean mix() {
        if (mixed) return false;
        if (val == 0) {
            mixed = true;
            return true;
        }

        DoubleLinkedListNode ins = this;
        for (int i = 0; i < Math.abs(val); i++) {
            ins = val < 0 ? ins.prev : ins.next;
        }

        detach();
        if (val < 0) {
            insertBefore(ins);
        } else {
            insertAfter(ins);
        }
        mixed = true;
        return true;
    }

    private void detach() {
        this.prev.next = next;
        this.next.prev = prev;
        this.prev = null;
        this.next = null;
    }

    private void insertAfter(DoubleLinkedListNode ins) {
        next = ins.next;
        prev = ins;
        prev.next = this;
        next.prev = this;
    }

    private void insertBefore(DoubleLinkedListNode ins) {
        next = ins;
        prev = ins.prev;
        prev.next = this;
        next.prev = this;
    }

    @Override
    public String toString() {
        return "(val=" + val + ", prev.val=" + prev.val + ", next.val=" + next.val + ")";
    }
}
