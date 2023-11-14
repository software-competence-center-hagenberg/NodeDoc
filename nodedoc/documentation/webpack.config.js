const path = require('path');

module.exports = {
    entry: './src/main/docscript/index.ts',
    output: {
        filename: 'docscript.js',
        path: path.resolve(__dirname, 'dist'),
    },
    module: {
        rules: [{
            test: /\.tsx?$/,
            loader: 'ts-loader',
        }, ],
    },
    resolve: {
        extensions: ['.tsx', '.ts', '.js'],
    },
}