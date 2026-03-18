import type { Metadata } from "next";
import { Outfit, Source_Code_Pro } from "next/font/google";
import "./globals.css";

const outfit = Outfit({
  variable: "--font-outfit",
  subsets: ["latin"],
});

const code = Source_Code_Pro({
  variable: "--font-code",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "HackGov - UBS Inteligente",
  description: "MVP de agendamento inteligente para UBS",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="pt-BR">
      <body className={`${outfit.variable} ${code.variable} antialiased`}>{children}</body>
    </html>
  );
}
