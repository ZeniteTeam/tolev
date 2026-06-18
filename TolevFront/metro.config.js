const path = require("path");
const { getDefaultConfig } = require("expo/metro-config");
const { withNativeWind } = require("nativewind/metro");

const config = getDefaultConfig(__dirname);

// zustand's ESM middleware build (esm/middleware.mjs) uses `import.meta`, which
// Metro can't transform for the web bundle — it throws at runtime:
//   "Cannot use 'import.meta' outside a module".
// On web, Metro's package-exports resolution auto-adds the `import` condition for
// ESM imports and picks that .mjs file. Redirect `zustand/middleware` to its
// equivalent CommonJS build (which guards dev checks with `process.env` instead).
// Native is unaffected: it already resolves the CJS build via the `react-native`
// condition.
const zustandMiddlewareCjs = path.join(
  path.dirname(require.resolve("zustand/package.json")),
  "middleware.js"
);

config.resolver.resolveRequest = (context, moduleName, platform) => {
  if (platform === "web" && moduleName === "zustand/middleware") {
    return { type: "sourceFile", filePath: zustandMiddlewareCjs };
  }
  return context.resolveRequest(context, moduleName, platform);
};

module.exports = withNativeWind(config, { input: "./global.css" });
