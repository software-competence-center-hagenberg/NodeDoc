import { compareAsc, parseISO } from "date-fns";

export function byDateString<T>(dateStringExtractor: (_: T) => string): (a: T, b: T) => number {
    return (a: T, b: T) => compareAsc(parseISO(dateStringExtractor(a)), parseISO(dateStringExtractor(b)));
}

export function byLocaleCompare<T>(stringExtractor: (_: T) => string): (a: T, b: T) => number {
    return (a: T, b: T) => stringExtractor(a).localeCompare(stringExtractor(b));
}

export function byMultiple<T>(sortFns: Array<(a: T, b: T) => number>): (a: T, b: T) => number {
    return (a: T, b: T) => {
        let i = 0;
        let result = 0;
        do {
            result = sortFns[i](a, b);
            i++;
        } while (i < sortFns.length && result === 0);
        return result;
    };
}
