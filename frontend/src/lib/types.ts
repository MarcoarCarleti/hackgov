export type DashboardResumo = {
  totalAgendamentos: number;
  totalRealizadas: number;
  totalFaltas: number;
  totalCancelamentos: number;
  taxaFaltas: number;
  taxaCancelamentos: number;
  vagasReaproveitadas: number;
};

export type IndicadorDia = {
  dia: string;
  quantidade: number;
};

export type ReaproveitamentoDia = {
  dia: string;
  vagasReaproveitadas: number;
};

export type RankingMedico = {
  medico: string;
  total: number;
};

export type SerieAgendamentoFalta = {
  dia: string;
  agendamentos: number;
  faltas: number;
};

export type Consulta = {
  id: number;
  usuarioId: number;
  usuarioNome: string;
  medicoId: number;
  medicoNome: string;
  especialidade: string;
  ubsId: number;
  ubsNome: string;
  agendaSlotId: number;
  status: string;
  dataConsulta: string;
  horaConsulta: string;
  observacoes: string;
  criadoEm: string;
  canceladoEm?: string;
  encaixeAutomatico: boolean;
};

export type AgendaDisponivel = {
  slotId: number;
  medicoId: number;
  medicoNome: string;
  especialidade: string;
  ubsId: number;
  ubsNome: string;
  data: string;
  horaInicio: string;
  horaFim: string;
};

export type Notificacao = {
  id: number;
  consultaId: number;
  tipo: string;
  dataEnvio: string;
  status: string;
  conteudo: string;
};
