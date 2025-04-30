import '@/styles/globals.css';
import { CssBaseline, ThemeProvider } from '@mui/material';
import type { AppProps } from 'next/app';
import Head from 'next/head';
import { theme } from '@/utils/theme';

export default function App({ Component, pageProps }: AppProps) {
  return (
    <>
      <Head>
        <title>AdOpt - Game Theory Ad Optimization</title>
        <meta name="description" content="Advanced ad optimization platform using game theory principles" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <link rel="icon" href="/favicon.ico" />
      </Head>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Component {...pageProps} />
      </ThemeProvider>
    </>
  );
} 