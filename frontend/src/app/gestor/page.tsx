"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";

export default function GestorIndexPage() {
  const router = useRouter();

  useEffect(() => {
    router.replace("/gestor/dashboard");
  }, [router]);

  return null;
}
