"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { getSession, Role, Session } from "./session";

export function useAuthGuard(allowedRoles?: Role[]) {
  const router = useRouter();
  const [session] = useState<Session | null>(() => getSession());
  const ready = !!session && (!allowedRoles || allowedRoles.includes(session.role));

  useEffect(() => {
    if (!session) {
      router.replace("/login");
      return;
    }

    if (allowedRoles && !allowedRoles.includes(session.role)) {
      if (session.role === "PACIENTE") router.replace("/cidadao");
      else router.replace("/gestor/dashboard");
    }
  }, [allowedRoles, router, session]);

  return { session, ready };
}
