import { NODE_WIDTH, SPLIT_TEXT_FONT_SIZE, STANDARD_FONT_SIZE } from "./constants";

/**
 * @deprecated
 * calculates the size a text needs with the specified font-size
 * @param {*} str 
 * @param {*} fontSize 
 */
function measureText(str: string, fontSize = 12) {
    const widths = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.2796875, 0.2765625, 0.3546875, 0.5546875, 0.5546875, 0.8890625, 0.665625, 0.190625, 0.3328125, 0.3328125, 0.3890625, 0.5828125, 0.2765625, 0.3328125, 0.2765625, 0.3015625, 0.5546875, 0.5546875, 0.5546875, 0.5546875, 0.5546875, 0.5546875, 0.5546875, 0.5546875, 0.5546875, 0.5546875, 0.2765625, 0.2765625, 0.584375, 0.5828125, 0.584375, 0.5546875, 1.0140625, 0.665625, 0.665625, 0.721875, 0.721875, 0.665625, 0.609375, 0.7765625, 0.721875, 0.2765625, 0.5, 0.665625, 0.5546875, 0.8328125, 0.721875, 0.7765625, 0.665625, 0.7765625, 0.721875, 0.665625, 0.609375, 0.721875, 0.665625, 0.94375, 0.665625, 0.665625, 0.609375, 0.2765625, 0.3546875, 0.2765625, 0.4765625, 0.5546875, 0.3328125, 0.5546875, 0.5546875, 0.5, 0.5546875, 0.5546875, 0.2765625, 0.5546875, 0.5546875, 0.221875, 0.240625, 0.5, 0.221875, 0.8328125, 0.5546875, 0.5546875, 0.5546875, 0.5546875, 0.3328125, 0.5, 0.2765625, 0.5546875, 0.5, 0.721875, 0.5, 0.5, 0.5, 0.3546875, 0.259375, 0.353125, 0.5890625]
    const avg = 0.528
    return str
        .split('')
        .map(c => c.charCodeAt(0) < widths.length ? widths[c.charCodeAt(0)] : avg)
        .reduce((cur, acc) => acc + cur) * fontSize + 40
}

/**
 * @deprecated
 * If the input parameter string is longer than 25 characters the most central capital letter is found
 * and the string is split into two parts and concatenated again. 
 * @param {*} str 
 */
 export function splitText(str: string) {
    // str = str.replace(/&lt;/gi, '<').replace(/&gt;/gi, '>'); // TODO
    if (str.length > 25) {
        const center = str.length / 2;
        var splitPos = center;
        for (var i = 0; i < center; ++i) {
            if (str.charAt(center + i) === str.charAt(center + i).toUpperCase()) {
                splitPos = center + i;
                break;
            } else if (str.charAt(center - i) === str.charAt(center - i).toUpperCase()) {
                splitPos = center - i;
                break;
            }
        }

        if (splitPos != 0 && splitPos != str.length) {
            var first = str.substr(0, splitPos);
            var second = str.substr(splitPos);
            var size = NODE_WIDTH;
            if (first.length > second.length)
                size = Math.max(size, measureText(first, SPLIT_TEXT_FONT_SIZE));
            else
                size = Math.max(size, measureText(second, SPLIT_TEXT_FONT_SIZE));

            return { label: first.concat("\n", second), size: size, split: true };
        }
    }
    return { label: str, size: Math.max(NODE_WIDTH, measureText(str, STANDARD_FONT_SIZE)), split: false };
}

// https://stackoverflow.com/questions/60884426/how-to-check-for-null-value-inline-and-throw-an-error-in-typescript
export function throwExpression(errorMessage: string): never {
    throw new Error(errorMessage);
}

// https://stackoverflow.com/questions/58960077/how-to-check-if-a-strongly-typed-object-contains-a-given-key-in-typescript-witho
export function isObjKey<T>(key: any, obj: T): key is keyof T {
    return key in obj;
}