import { create } from 'zustand'
// import { createStore } from 'zustand/vanilla'

type ProjectType = {
  inboxId: string;
  setInboxId: (id: string) => void
}

export const useInboxIdStore = create<ProjectType>(set => ({
  inboxId: '',
  setInboxId: inboxId => set({ inboxId }),
}))

// export const store = createStore<ProjectType>(set => ({
//   inboxId: '',
//   setInboxId: inboxId => set({ inboxId })
// }))
