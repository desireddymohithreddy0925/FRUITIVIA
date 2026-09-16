import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import type { UseQueryOptions, UseMutationOptions } from '@tanstack/react-query';
import api from '../services/api';

export interface PaginatedResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export function createApiHooks<T, C = Partial<T>, U = Partial<T>>(resourcePath: string, queryKeyName: string) {
  
  // 1. Get Many (Paginated/Filtered)
  const useGetMany = (params?: Record<string, any>, options?: Omit<UseQueryOptions<PaginatedResponse<T>>, 'queryKey' | 'queryFn'>) => {
    return useQuery<PaginatedResponse<T>>({
      queryKey: [queryKeyName, params],
      queryFn: async () => {
        const { data } = await api.get(resourcePath, { params });
        return data;
      },
      ...options,
    });
  };

  // 2. Get One
  const useGetOne = (id: string | number, options?: Omit<UseQueryOptions<T>, 'queryKey' | 'queryFn'>) => {
    return useQuery<T>({
      queryKey: [queryKeyName, id],
      queryFn: async () => {
        const { data } = await api.get(`${resourcePath}/${id}`);
        return data;
      },
      enabled: !!id,
      ...options,
    });
  };

  // 3. Create
  const useCreate = (options?: Omit<UseMutationOptions<T, Error, C>, 'mutationFn'>) => {
    const queryClient = useQueryClient();
    return useMutation<T, Error, C>({
      mutationFn: async (payload) => {
        const { data } = await api.post(resourcePath, payload);
        return data;
      },
      onSuccess: (...args) => {
        queryClient.invalidateQueries({ queryKey: [queryKeyName] });
        if (options?.onSuccess) options.onSuccess(...args);
      },
      ...options,
    });
  };

  // 4. Update
  const useUpdate = (options?: Omit<UseMutationOptions<T, Error, { id: string | number; payload: U }>, 'mutationFn'>) => {
    const queryClient = useQueryClient();
    return useMutation<T, Error, { id: string | number; payload: U }>({
      mutationFn: async ({ id, payload }) => {
        const { data } = await api.put(`${resourcePath}/${id}`, payload);
        return data;
      },
      onSuccess: (...args) => {
        queryClient.invalidateQueries({ queryKey: [queryKeyName] });
        if (options?.onSuccess) options.onSuccess(...args);
      },
      ...options,
    });
  };

  // 5. Delete
  const useRemove = (options?: Omit<UseMutationOptions<void, Error, string | number>, 'mutationFn'>) => {
    const queryClient = useQueryClient();
    return useMutation<void, Error, string | number>({
      mutationFn: async (id) => {
        await api.delete(`${resourcePath}/${id}`);
      },
      onSuccess: (...args) => {
        queryClient.invalidateQueries({ queryKey: [queryKeyName] });
        if (options?.onSuccess) options.onSuccess(...args);
      },
      ...options,
    });
  };

  return {
    useGetMany,
    useGetOne,
    useCreate,
    useUpdate,
    useRemove,
  };
}
