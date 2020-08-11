package com.example.appactionvisualizer.databean;

// A trivial tuple class used for collection of heterogeneous classes
public class Tuple<L, M, R> {
  public L left;
  public M mid;
  public R right;

  public Tuple(L left, M mid, R right) {
    this.left= left;
    this.mid = mid;
    this.right = right;
  }

  @Override
  public int hashCode() {
    return left.hashCode() ^ mid.hashCode() ^ right.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Tuple)) {
      return false;
    }
    Tuple tuple = (Tuple) o;
    return this.left.equals(tuple.left) &&
        this.mid.equals(tuple.mid) &&
        this.right.equals(tuple.right);
  }
}
