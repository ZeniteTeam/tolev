/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./App.{js,jsx,ts,tsx}", "./src/**/*.{js,jsx,ts,tsx}"],
  presets: [require("nativewind/preset")],
  theme: {
    extend: {
      colors: {
        bg: "#F9FBFA",
        surface: "#FFFFFF",
        ink: "#1E2A25",
        "ink-soft": "#161C1B",
        muted: "#6B7D75",
        line: "#A4A7A5",
        "line-soft": "#D9D9D9",
        primary: {
          900: "#00422F",
          800: "#004E31",
          700: "#03643F",
          600: "#136F4A",
          500: "#1CA474",
          400: "#1DA473",
          300: "#7DCDA8",
          200: "#ACDBCA",
          100: "#CDEADF",
          50: "#E7EFEA",
          25: "#E9F5F1",
        },
        teal: {
          500: "#30BCB3",
          400: "#6FE3D6",
          300: "#9AE0D6",
        },
        coral: {
          500: "#FE6F50",
          300: "#FEAC96",
        },
        info: {
          700: "#19375B",
          300: "#ADC9EC",
        },
      },
      fontFamily: {
        regular: ["PlusJakartaSans_400Regular"],
        medium: ["PlusJakartaSans_500Medium"],
        semibold: ["PlusJakartaSans_600SemiBold"],
        bold: ["PlusJakartaSans_700Bold"],
      },
    },
  },
  plugins: [],
};
