package jsettlers.graphics.image.reader;

import java.util.*;

public final class Hashes {

    private final List<Long> hashes;

    Hashes(List<Long> hashes) {
        this.hashes = hashes;
    }

    public int[] compareAndCreateMapping(Hashes other) {
        int[] mapping = new int[hashes.size()];

        for (int i1 = 0; i1 < hashes.size(); i1++) {
            Long h1 = hashes.get(i1);
            int i2 = i1 < other.hashes.size()
                    && h1.equals(other.hashes.get(i1)) ? i1 : other.hashes.indexOf(h1);
            mapping[i1] = i2;
            System.out.println(i1 + " -> " + i2);
        }

        return mapping;
    }

    public String hash() {
        long hashCode = 1L;
        long multiplier = 1L;
        for (Long hash : hashes) {
            multiplier *= 31L;
            hashCode += (hash + 27L) * multiplier;
        }
        return Long.toString(hashCode);
    }

    public static final class Builder {

        private final ArrayList<Long> hashes = new ArrayList<>();

        public Builder add(Hashes hashes) {
            this.hashes.addAll(hashes.hashes);
            return this;
        }

        public Hashes build() {
            return new Hashes(hashes);
        }
    }
}
